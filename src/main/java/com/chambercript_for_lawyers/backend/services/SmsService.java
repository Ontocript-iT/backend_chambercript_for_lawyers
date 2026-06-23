package com.chambercript_for_lawyers.backend.services;


import com.chambercript_for_lawyers.backend.enums.SmsPlan;
import com.chambercript_for_lawyers.backend.model.Subscription;
import com.chambercript_for_lawyers.backend.model.SubscriptionUsage;
import com.chambercript_for_lawyers.backend.repository.SubscriptionRepository;
import com.chambercript_for_lawyers.backend.repository.SubscriptionUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // System.out.println වෙනුවට Log භාවිතා කිරීම සඳහා
public class SmsService {

    @Value("${textlk.api.url}")
    private String apiUrl;

    @Value("${textlk.api.token}")
    private String apiToken;

    @Value("${textlk.sender.id}")
    private String senderId;


    private final RestTemplate restTemplate = new RestTemplate();

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionUsageRepository subscriptionUsageRepository;

    @Transactional
    public void sendSms(String phoneNumber, String messageText) {
        try {
            if (phoneNumber.startsWith("0")) {
                phoneNumber = "94" + phoneNumber.substring(1);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiToken);

            Map<String, String> body = new HashMap<>();
            body.put("recipient", phoneNumber);
            body.put("sender_id", senderId);
            body.put("message", messageText);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            // Send POST request to Text.lk
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                log.info("SMS sent successfully to {}", phoneNumber);
            } else {
                log.error("Failed to send SMS. Response: {}", response.getBody());
                throw new RuntimeException("Failed to send SMS. API Response: " + response.getBody());
            }
        } catch (Exception e) {
            log.error("SMS Sending Error: {}", e.getMessage());
            throw new RuntimeException("Error occurred while sending SMS", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean canSendSmsAndIncrement(Long adminId) {
        Subscription subscription = subscriptionRepository.findByAdminId(adminId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        Optional<SubscriptionUsage> usage = subscriptionUsageRepository.findBySubscriptionId(subscription.getId());

        SmsPlan currentSmsPlan = subscription.getSmsPlan();

//        if (currentSmsPlan == SmsPlan.NONE) {
//            return false;
//        }

        if (usage.isPresent()) {
            log.info("Found subscription usage for subscription ID: {}", subscription.getId());
            SubscriptionUsage subscriptionUsage = usage.get();
            int smsUsed = subscriptionUsage.getUsedSmsCount();
            int smsLimit = currentSmsPlan.getQuota();

            if (smsUsed < smsLimit) {
                log.info("Current SMS usage: {}/{}", smsUsed, smsLimit);
//                subscriptionUsage.setUsedSmsCount(smsUsed + 1);
                subscriptionUsageRepository.incrementSmsCountById(subscription.getId());
                return true;
            } else {
                log.warn("SMS limit reached for Admin ID: {}", adminId);
                return false;
            }
        }

        return false;
    }

    public boolean checkSmsCanSend(Long adminId) {
        Subscription subscription = subscriptionRepository.findByAdminId(adminId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        Optional<SubscriptionUsage> usage = subscriptionUsageRepository.findBySubscriptionId(subscription.getId());

        SmsPlan currentSmsPlan = subscription.getSmsPlan();

        if (currentSmsPlan == SmsPlan.NONE) {
            return false;
        }

        if (usage.isPresent()) {
            log.info("Found subscription usage for subscription ID: {}", subscription.getId());
            SubscriptionUsage subscriptionUsage = usage.get();
            int smsUsed = subscriptionUsage.getUsedSmsCount();
            int smsLimit = currentSmsPlan.getQuota();

            log.info("Current SMS usage: {}/{}", smsUsed, smsLimit);
            return smsUsed < smsLimit;
        }

        return false;
    }


    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetMonthlySmsUsage() {
        log.info("Starting monthly SMS usage reset for active NONE plans...");

        int updatedRecords = subscriptionUsageRepository.resetSmsUsageForNonePlans();

        log.info("Successfully reset SMS usage to 0 for {} subscription(s).", updatedRecords);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
    public void processMonthlySmsRollover() {
        log.info("Starting monthly SMS plan evaluation and usage reset...");

        List<SubscriptionUsage> paidUsages = subscriptionUsageRepository.findActivePaidPlanUsages();
        int downgradedCount = 0;

        for (SubscriptionUsage usage : paidUsages) {
            Subscription subscription = usage.getSubscription();
            SmsPlan currentPlan = subscription.getSmsPlan();

            int smsUsed = usage.getUsedSmsCount();
            int smsLimit = currentPlan.getQuota();

            if (smsUsed >= smsLimit) {
                log.info("Subscription ID {} exceeded quota ({}/{}). Downgrading to NONE.",
                        subscription.getId(), smsUsed, smsLimit);

                subscription.setSmsPlan(SmsPlan.NONE);
                downgradedCount++;
            }

            usage.setUsedSmsCount(0);
        }

        if (!paidUsages.isEmpty()) {
            subscriptionUsageRepository.saveAll(paidUsages);
        }

        int resetNoneCount = subscriptionUsageRepository.resetSmsUsageForNonePlans();

        log.info("Monthly SMS rollover complete. Downgraded {} plans to NONE. Reset usage for {} NONE plans.",
                downgradedCount, resetNoneCount);
    }
}