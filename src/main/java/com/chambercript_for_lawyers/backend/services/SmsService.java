package com.chambercript_for_lawyers.backend.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmsService {

    @Value("${textlk.api.url}")
    private String apiUrl;

    @Value("${textlk.api.token}")
    private String apiToken;

    @Value("${textlk.sender.id}")
    private String senderId;

    private final RestTemplate restTemplate = new RestTemplate();

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
                System.out.println("SMS sent successfully to " + phoneNumber);
            } else {
                System.err.println("Failed to send SMS. Response: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("SMS Sending Error: " + e.getMessage());
        }
    }
}