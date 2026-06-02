package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.model.Hearing;
import com.chambercript_for_lawyers.backend.services.SmsService;
import com.chambercript_for_lawyers.backend.services.central.HearingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/hearings")
@RequiredArgsConstructor
public class HearingController {

    private final HearingService hearingService;

    private final SmsService smsService;


    @PostMapping("/case/{caseId}/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> saveHearing(@PathVariable Long caseId, @RequestBody Hearing hearingRequest) {
        return hearingService.saveHearing(caseId, hearingRequest);
    }

    @GetMapping("/case/{caseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getHearingsByCase(@PathVariable Long caseId) {
        return hearingService.getHearingsByCase(caseId);

    }

    @PostMapping("/send-sms")
    public ResponseEntity<?> testSmsConnection(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extract data from the Postman JSON body
            String phoneNumber = payload.get("phoneNumber");
            String message = payload.get("message");

            // Validate inputs
            if (phoneNumber == null || message == null) {
                response.put("status", 400);
                response.put("message", "Please provide both 'phoneNumber' and 'message' in the JSON body.");
                return ResponseEntity.badRequest().body(response);
            }

            // Call the existing SMS Service
            smsService.sendSms(phoneNumber, message);

            response.put("status", 200);
            response.put("message", "SMS request processed successfully. Check console/logs for Text.lk response.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}