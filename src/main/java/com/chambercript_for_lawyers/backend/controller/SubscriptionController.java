package com.chambercript_for_lawyers.backend.controller;
import com.chambercript_for_lawyers.backend.dto.request.SubscriptionRequest;
import com.chambercript_for_lawyers.backend.repository.UserRepository;
import com.chambercript_for_lawyers.backend.services.central.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    @PostMapping("/choose")
    public ResponseEntity<?> choosePlan(Principal principal, @RequestBody SubscriptionRequest request) {
        Long adminId = getUserIdFromPrincipal(principal);
        return subscriptionService.choosePlan(adminId, request);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updatePlan(Principal principal, @RequestBody SubscriptionRequest request) {
        Long adminId = getUserIdFromPrincipal(principal);
        return subscriptionService.updatePlan(adminId, request);
    }

    // Helper to get Admin ID safely
    private Long getUserIdFromPrincipal(Principal principal) {
        if (principal == null) throw new RuntimeException("Unauthorized");
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}