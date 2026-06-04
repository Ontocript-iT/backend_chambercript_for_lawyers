package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.model.Subscription;
import com.chambercript_for_lawyers.backend.model.SubscriptionUsage;
import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.repository.SubscriptionRepository;
import com.chambercript_for_lawyers.backend.repository.SubscriptionUsageRepository;
import com.chambercript_for_lawyers.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminActivityServiceImpl {

    private final SubscriptionUsageRepository usageRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final UserRepository userRepository;


    @Transactional
    public void trackFileUpload(String lawFirmCode, String uploadedBy, String fileName, double fileSizeMb) throws Exception {

        Optional<User> userOpt = userRepository.findByLawFirmCode(lawFirmCode);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found: " + uploadedBy);
        }
        User user = userOpt.get();

        SubscriptionUsage usage = usageRepository.findBySubscriptionAdminId(user.getId());
        if (usage == null) {
            throw new Exception("Subscription usage not found for admin ID: " + user.getId());
        }

        Subscription subscription = usage.getSubscription();

        double maxStorageMb = subscription.getMaxStorageGb() * 1024.0;

        double projectedStorageMb = usage.getUsedStorageMb() + fileSizeMb;

        if (projectedStorageMb > maxStorageMb) {
            throw new Exception("Storage limit exceeded. Upgrade your plan.");
        }

        usageRepository.incrementStorageUsed(subscription.getId(), fileSizeMb);
    }


    @Transactional
    public ResponseEntity<?> trackEmployeeCreation(Long adminId) {
        SubscriptionUsage usage = usageRepository.findBySubscriptionAdminId(adminId);

        if (usage == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Subscription usage not found for admin ID: " + adminId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404
        }

        Subscription subscription = usage.getSubscription();

        System.out.println("Current Employees: " + usage.getCurrentEmployeesCount() +
                ", Max Employees: " + subscription.getMaxEmployees());

        if (usage.getCurrentEmployeesCount() >= subscription.getMaxEmployees()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Employee limit exceeded. Upgrade your plan.");
            errorResponse.put("status", HttpStatus.FORBIDDEN.value());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        usageRepository.incrementEmployeeCount(subscription.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee count incremented successfully");
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}