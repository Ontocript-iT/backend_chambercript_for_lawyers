package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.services.central.SubscriptionService;
import com.chambercript_for_lawyers.backend.services.central.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/superAdmin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final SubscriptionService subscriptionService;

    private final UserService userService;

    @GetMapping("/subscriptions")
    @PreAuthorize(("hasRole('SUPER_ADMIN')"))
    public ResponseEntity<?> getAllSubscriptions(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return subscriptionService.getAllSubscriptions(page, size);

    }

    @PostMapping("/activeSubscriptionById/{id}")
    @PreAuthorize(("hasRole('SUPER_ADMIN')"))
    public ResponseEntity<?> activeSubscriptionById(@PathVariable Long id) {
        return subscriptionService.activeSubscriptionById(id);
    }

    @GetMapping("/getInactiveSubscriptions")
    @PreAuthorize(("hasRole('SUPER_ADMIN')"))
    public ResponseEntity<?> getInactiveSubscriptions(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        return subscriptionService.getInactiveSubscriptions(page, size);
    }

    @PostMapping("/searchSubscriptionsByAdminEmailOrNic/{query}")
    @PreAuthorize(("hasRole('SUPER_ADMIN')"))
    public ResponseEntity<?> searchSubscriptionsByAdminEmailOrNic(@PathVariable String query) {
        return subscriptionService.searchSubscriptionsByAdminEmailOrNic(query);
    }

    @GetMapping("/getAllLawFirms")
    @PreAuthorize(("hasRole('SUPER_ADMIN')"))
    public ResponseEntity<?> getAllLawFirms(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return userService.getAllLawFirms(page, size);
    }


    @PostMapping("/searchLawFirmsByLawFirmCode/{lawFirmCode}")
    @PreAuthorize(("hasRole('SUPER_ADMIN')"))
    public ResponseEntity<?> searchLawFirmsByLawFirmCode(@PathVariable String lawFirmCode) {
        return userService.searchLawFirmsByLawFirmCode(lawFirmCode);
    }

}
