package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.dto.request.SubscriptionRequest;
import com.chambercript_for_lawyers.backend.dto.response.SubscriptionResponseDTO;
import com.chambercript_for_lawyers.backend.enums.SmsPlan;
import com.chambercript_for_lawyers.backend.model.PlanType;
import com.chambercript_for_lawyers.backend.model.Subscription;
import com.chambercript_for_lawyers.backend.model.SubscriptionUsage;
import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.repository.SubscriptionRepository;
import com.chambercript_for_lawyers.backend.repository.SubscriptionUsageRepository;
import com.chambercript_for_lawyers.backend.repository.UserRepository;
import com.chambercript_for_lawyers.backend.services.central.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    private final SubscriptionUsageRepository subscriptionUsageRepository;


    @Override
    public ResponseEntity<?> choosePlan(Long adminId, SubscriptionRequest request) {
        HashMap<String, Object> response = new HashMap<>();

        System.out.println("Received subscription request: " + request + " for admin ID: " + adminId);

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

            if (request.getSmsPlan() != null) {
                subscription.setSmsPlan(request.getSmsPlan());
                subscription.setActiveSmsPlan(true);
            }
            subscription = subscriptionRepository.save(subscription);

            SubscriptionUsage usage = new SubscriptionUsage();
            usage.setSubscription(subscription);
            usage.setUsedSmsCount(0);
            usage.setCurrentEmployeesCount(0);
            usage.setUsedStorageMb(0.0);
            subscriptionUsageRepository.save(usage);

            SubscriptionResponseDTO cleanData = SubscriptionResponseDTO.builder()
                    .id(subscription.getId())
                    .planType(subscription.getPlanType().name())
                    .smsPlanType(subscription.getSmsPlan().name())
                    .smsQuota(subscription.getSmsPlan().getQuota())
                    .maxEmployees(subscription.getMaxEmployees())
                    .maxStorageGb(subscription.getMaxStorageGb())
                    .isActive(subscription.isActive())
                    .adminId(subscription.getAdmin().getId())
                    .adminName(subscription.getAdmin().getFirstName() + " " + subscription.getAdmin().getLastName())
                    .build();

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

//            if(!subOpt.get().isActive()) {
//                response.put("status", 400);
//                response.put("message", "Subscription is currently activation pending. Please wait until it is active before updating the plan.");
//                return ResponseEntity.status(400).body(response);
//            }

            Subscription existingSub = subOpt.get();

            if (isDowngrade(existingSub.getPlanType(), request.getPlanType())) {
                if (existingSub.getUpdatedAt() != null) {
                    long daysSinceLastUpdate = ChronoUnit.DAYS.between(existingSub.getUpdatedAt(), LocalDateTime.now());

                    if (daysSinceLastUpdate < 7) {
                        long daysLeft = 7 - daysSinceLastUpdate;
                        response.put("status", 400);
                        response.put("message", "You cannot downgrade your plan immediately. Please wait " + daysLeft + " more day(s) (1 week total).");
                        return ResponseEntity.status(400).body(response);
                    }
                }
            }
            applyLimits(existingSub, request);

            SubscriptionResponseDTO cleanData = SubscriptionResponseDTO.builder()
                    .id(existingSub.getId())
                    .planType(existingSub.getPlanType().name())
                    .maxEmployees(existingSub.getMaxEmployees())
                    .maxStorageGb(existingSub.getMaxStorageGb())
                    .adminId(existingSub.getAdmin().getId())
                    .isActive(false)
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

    private boolean isDowngrade(PlanType current, PlanType requested) {
        if (current == null || requested == null) return false;
        return current.ordinal() > requested.ordinal();
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
                subscription.setActive(false);
                break;
            case PRO:
                subscription.setMaxEmployees(7);
                subscription.setMaxStorageGb(50);
                subscription.setActive(false);
                break;
            case CUSTOM:
                if (request.getCustomMaxEmployees() == null || request.getCustomMaxStorageGb() == null) {
                    throw new IllegalArgumentException("Custom plan requires customMaxEmployees and customMaxStorageGb values.");
                }
                subscription.setMaxEmployees(request.getCustomMaxEmployees());
                subscription.setMaxStorageGb(request.getCustomMaxStorageGb());
                subscription.setActive(false);
                break;
            default:
                throw new IllegalArgumentException("Invalid plan type provided.");
        }
    }

    @Override
    public ResponseEntity<?> getCurrentSubscription(Long adminId) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Optional<Subscription> subOpt = subscriptionRepository.findByAdminId(adminId);

            if (subOpt.isEmpty()) {
                response.put("status", 404);
                response.put("message", "No active subscription found for this admin.");
                return ResponseEntity.status(404).body(response);
            }

            Subscription sub = subOpt.get();
            SubscriptionResponseDTO cleanData = SubscriptionResponseDTO.builder()
                    .id(sub.getId())
                    .planType(sub.getPlanType().name())
                    .maxEmployees(sub.getMaxEmployees())
                    .maxStorageGb(sub.getMaxStorageGb())
                    .smsPlanType(sub.getSmsPlan() != null ? sub.getSmsPlan().name() : null)
                    .smsQuota(sub.getSmsPlan() != null ? sub.getSmsPlan().getQuota() : null)
                    .isActive(sub.isActive())
                    .adminId(sub.getAdmin().getId())
                    .adminName(sub.getAdmin().getFirstName() + " " + sub.getAdmin().getLastName())
                    .build();

            response.put("status", 200);
            response.put("message", "Current subscription retrieved successfully.");
            response.put("data", cleanData);
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Override
    public ResponseEntity<?> getAllSubscriptions(int page, int size) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<Subscription> subscriptionPage = subscriptionRepository.findAll(pageable);

            List<SubscriptionResponseDTO> cleanData = subscriptionPage.getContent().stream()
                    .map(sub -> SubscriptionResponseDTO.builder()
                            .id(sub.getId())
                            .planType(sub.getPlanType().name())
                            .maxEmployees(sub.getMaxEmployees())
                            .maxStorageGb(sub.getMaxStorageGb())
                            .isActive(sub.isActive())
                            .adminId(sub.getAdmin().getId())
                            .adminName(sub.getAdmin().getFirstName() + " " + sub.getAdmin().getLastName())
                            .build())
                    .collect(Collectors.toList());

            response.put("status", 200);
            response.put("message", "All subscriptions retrieved successfully.");
            response.put("data", cleanData);

            response.put("currentPage", subscriptionPage.getNumber());
            response.put("totalItems", subscriptionPage.getTotalElements());
            response.put("totalPages", subscriptionPage.getTotalPages());
            response.put("pageSize", subscriptionPage.getSize());

            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Override
    public ResponseEntity<?> activeSubscriptionById(Long subscriptionId) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Optional<Subscription> subOpt = subscriptionRepository.findById(subscriptionId);

            if (subOpt.isEmpty()) {
                response.put("status", 404);
                response.put("message", "Subscription not found.");
                return ResponseEntity.status(404).body(response);
            }

            Subscription sub = subOpt.get();
            sub.setActive(true);
            subscriptionRepository.save(sub);

            SubscriptionResponseDTO cleanData = SubscriptionResponseDTO.builder()
                    .id(sub.getId())
                    .planType(sub.getPlanType().name())
                    .maxEmployees(sub.getMaxEmployees())
                    .maxStorageGb(sub.getMaxStorageGb())
                    .isActive(sub.isActive())
                    .adminId(sub.getAdmin().getId())
                    .adminName(sub.getAdmin().getFirstName() + " " + sub.getAdmin().getLastName())
                    .build();

            response.put("status", 200);
            response.put("message", "Subscription activated successfully.");
            response.put("data", cleanData);
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> searchSubscriptionsByAdminEmailOrNic(String query) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            var subscriptions = subscriptionRepository.findSubscriptionsByAdminEmailOrNic(query);

            var cleanData = subscriptions.stream().map(sub -> SubscriptionResponseDTO.builder()
                    .id(sub.getId())
                    .planType(sub.getPlanType().name())
                    .maxEmployees(sub.getMaxEmployees())
                    .maxStorageGb(sub.getMaxStorageGb())
                    .isActive(sub.isActive())
                    .adminId(sub.getAdmin().getId())
                    // Safe to call getAdmin() here because of JOIN FETCH
                    .adminName(sub.getAdmin().getFirstName() + " " + sub.getAdmin().getLastName())
                    .build()).toList();

            response.put("status", 200);
            response.put("message", "Subscriptions matching query retrieved successfully.");
            response.put("data", cleanData);

            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Override
    public ResponseEntity<?> getInactiveSubscriptions(int page, int size) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<Subscription> subscriptionPage = subscriptionRepository.findByIsActiveFalse(pageable);

            List<SubscriptionResponseDTO> cleanData = subscriptionPage.getContent().stream()
                    .map(sub -> SubscriptionResponseDTO.builder()
                            .id(sub.getId())
                            .planType(sub.getPlanType().name())
                            .maxEmployees(sub.getMaxEmployees())
                            .maxStorageGb(sub.getMaxStorageGb())
                            .isActive(sub.isActive())
                            .adminId(sub.getAdmin().getId())
                            .adminName(sub.getAdmin().getFirstName() + " " + sub.getAdmin().getLastName())
                            .build())
                    .collect(Collectors.toList());

            response.put("status", 200);
            response.put("message", "Inactive subscriptions retrieved successfully.");
            response.put("data", cleanData);

            response.put("currentPage", subscriptionPage.getNumber());
            response.put("totalItems", subscriptionPage.getTotalElements());
            response.put("totalPages", subscriptionPage.getTotalPages());
            response.put("pageSize", subscriptionPage.getSize());

            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Override
    public ResponseEntity<?> updateSmsPlan(Long adminId, SubscriptionRequest request) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Optional<Subscription> subOpt = subscriptionRepository.findByAdminId(adminId);

            if (subOpt.isEmpty()) {
                response.put("status", 404);
                response.put("message", "Subscription not found.");
                return ResponseEntity.status(404).body(response);
            }

            Subscription sub = subOpt.get();
            sub.setSmsPlan(request.getSmsPlan());
            sub.setActiveSmsPlan(false);
            subscriptionRepository.save(sub);

            SubscriptionResponseDTO cleanData = SubscriptionResponseDTO.builder()
                    .id(sub.getId())
                    .planType(sub.getPlanType().name())
                    .maxEmployees(sub.getMaxEmployees())
                    .maxStorageGb(sub.getMaxStorageGb())
                    .isActive(sub.isActive())
                    .adminId(sub.getAdmin().getId())
                    .adminName(sub.getAdmin().getFirstName() + " " + sub.getAdmin().getLastName())
                    .smsPlan(sub.getSmsPlan())
                    .build();

            response.put("status", 200);
            response.put("message", "SMS plan updated successfully.");
            response.put("data", cleanData);
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


    @Override
    public ResponseEntity<?> getRemainingSms(Long adminId) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Optional<Subscription> subOpt = subscriptionRepository.findByAdminId(adminId);

            if (subOpt.isEmpty()) {
                response.put("status", 404);
                response.put("message", "Subscription not found.");
                return ResponseEntity.status(404).body(response);
            }

            Subscription sub = subOpt.get();

            response.put("status", 200);
            response.put("message", "Remaining SMS retrieved successfully.");
            response.put("data", getRemainingSmsByAdminId(adminId));
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    public Integer getRemainingSmsByAdminId(Long adminId) {
        Subscription subscription = subscriptionRepository.findByAdminId(adminId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        Optional<SubscriptionUsage> usage = subscriptionUsageRepository.findBySubscriptionId(subscription.getId());

        SmsPlan currentSmsPlan = subscription.getSmsPlan();


        if (usage.isPresent()) {
            SubscriptionUsage subscriptionUsage = usage.get();
            int smsUsed = subscriptionUsage.getUsedSmsCount();
            int smsLimit = currentSmsPlan.getQuota();

            return Math.max(smsLimit - smsUsed, 0);


        }
        return currentSmsPlan.getQuota();
    }

    @Override
    public ResponseEntity<?> updateSmsPlanStatus(Long adminId, boolean isActive) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Optional<Subscription> subOpt = subscriptionRepository.findByAdminId(adminId);

            if (subOpt.isEmpty()) {
                response.put("status", 404);
                response.put("message", "Subscription not found.");
                return ResponseEntity.status(404).body(response);
            }

            Subscription sub = subOpt.get();
            sub.setActiveSmsPlan(isActive);
            subscriptionRepository.save(sub);

            SubscriptionResponseDTO cleanData = SubscriptionResponseDTO.builder()
                    .id(sub.getId())
                    .planType(sub.getPlanType().name())
                    .maxEmployees(sub.getMaxEmployees())
                    .maxStorageGb(sub.getMaxStorageGb())
                    .isActive(sub.isActive())
                    .adminId(sub.getAdmin().getId())
                    .adminName(sub.getAdmin().getFirstName() + " " + sub.getAdmin().getLastName())
                    .smsPlan(sub.getSmsPlan())
                    .isActiveSmsPlan(sub.isActiveSmsPlan())
                    .build();

            response.put("status", 200);
            response.put("message", "SMS plan status updated successfully.");
            response.put("data", cleanData);
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}