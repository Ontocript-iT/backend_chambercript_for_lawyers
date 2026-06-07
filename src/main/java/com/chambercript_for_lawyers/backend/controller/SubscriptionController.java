package com.chambercript_for_lawyers.backend.controller;
import com.chambercript_for_lawyers.backend.dto.request.SubscriptionRequest;
import com.chambercript_for_lawyers.backend.repository.UserRepository;
import com.chambercript_for_lawyers.backend.services.central.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> choosePlan(Principal principal, @RequestBody SubscriptionRequest request) {
        Long adminId = getUserIdFromPrincipal(principal);
        return subscriptionService.choosePlan(adminId, request);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePlan(Principal principal, @RequestBody SubscriptionRequest request) {
        Long adminId = getUserIdFromPrincipal(principal);
        return subscriptionService.updatePlan(adminId, request);
    }

    @PutMapping("/updateSmsPlan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSmsPlan(Principal principal, @RequestBody SubscriptionRequest request) {
        Long adminId = getUserIdFromPrincipal(principal);
        return subscriptionService.updateSmsPlan(adminId, request);
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        if (principal == null) throw new RuntimeException("Unauthorized");
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @GetMapping("/current")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCurrentSubscription(Principal principal) {
        Long adminId = getUserIdFromPrincipal(principal);
        return subscriptionService.getCurrentSubscription(adminId);
    }
}