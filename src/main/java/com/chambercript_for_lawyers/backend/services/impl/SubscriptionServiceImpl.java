package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.dto.request.SubscriptionRequest;
import com.chambercript_for_lawyers.backend.dto.response.SubscriptionResponseDTO;
import com.chambercript_for_lawyers.backend.model.Subscription;
import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.repository.SubscriptionRepository;
import com.chambercript_for_lawyers.backend.repository.UserRepository;
import com.chambercript_for_lawyers.backend.services.central.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;


    @Override
    public ResponseEntity<?> choosePlan(Long adminId, SubscriptionRequest request) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Optional<User> adminOpt = userRepository.findById(adminId);
            if (adminOpt.isEmpty()) {
                response.put("status", 404);
                response.put("message", "Admin not found");
                return ResponseEntity.status(404).body(response);
            }

            if (subscriptionRepository.findByAdminId(adminId).isPresent()) {
                response.put("status", 400);
                response.put("message", "Admin already has an active subscription. Please use update instead.");
                return ResponseEntity.status(400).body(response);
            }

            Subscription subscription = buildSubscription(adminOpt.get(), request);

            SubscriptionResponseDTO cleanData = SubscriptionResponseDTO.builder()
                    .id(subscription.getId())
                    .planType(subscription.getPlanType().name())
                    .maxEmployees(subscription.getMaxEmployees())
                    .maxStorageGb(subscription.getMaxStorageGb())
                    .adminId(subscription.getAdmin().getId())
                    .adminName(subscription.getAdmin().getFirstName() + " " + subscription.getAdmin().getLastName())
                    .build();
            subscriptionRepository.save(subscription);

            response.put("status", 201);
            response.put("message", "Subscription plan " + request.getPlanType() + " activated successfully.");
            response.put("data", cleanData);
            return ResponseEntity.status(201).body(response);

        } catch (IllegalArgumentException e) {
            response.put("status", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Override
    public ResponseEntity<?> updatePlan(Long adminId, SubscriptionRequest request) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Optional<Subscription> subOpt = subscriptionRepository.findByAdminId(adminId);

            if (subOpt.isEmpty()) {
                response.put("status", 404);
                response.put("message", "No active subscription found for this admin.");
                return ResponseEntity.status(404).body(response);
            }

            Subscription existingSub = subOpt.get();
            applyLimits(existingSub, request);

            SubscriptionResponseDTO cleanData = SubscriptionResponseDTO.builder()
                    .id(existingSub.getId())
                    .planType(existingSub.getPlanType().name())
                    .maxEmployees(existingSub.getMaxEmployees())
                    .maxStorageGb(existingSub.getMaxStorageGb())
                    .adminId(existingSub.getAdmin().getId())
                    .adminName(existingSub.getAdmin().getFirstName() + " " + existingSub.getAdmin().getLastName())
                    .build();

            subscriptionRepository.save(existingSub);

            response.put("status", 200);
            response.put("message", "Subscription plan updated to " + request.getPlanType() + " successfully.");
            response.put("data", cleanData);
            return ResponseEntity.status(200).body(response);

        } catch (IllegalArgumentException e) {
            response.put("status", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


    private Subscription buildSubscription(User admin, SubscriptionRequest request) {
        Subscription subscription = new Subscription();
        subscription.setAdmin(admin);
        applyLimits(subscription, request);
        return subscription;
    }

    private void applyLimits(Subscription subscription, SubscriptionRequest request) {
        subscription.setPlanType(request.getPlanType());

        switch (request.getPlanType()) {
            case STANDARD:
                subscription.setMaxEmployees(2);
                subscription.setMaxStorageGb(20);
                break;
            case PRO:
                subscription.setMaxEmployees(7);
                subscription.setMaxStorageGb(50);
                break;
            case CUSTOM:
                if (request.getCustomMaxEmployees() == null || request.getCustomMaxStorageGb() == null) {
                    throw new IllegalArgumentException("Custom plan requires customMaxEmployees and customMaxStorageGb values.");
                }
                subscription.setMaxEmployees(request.getCustomMaxEmployees());
                subscription.setMaxStorageGb(request.getCustomMaxStorageGb());
                break;
            default:
                throw new IllegalArgumentException("Invalid plan type provided.");
        }
    }
}