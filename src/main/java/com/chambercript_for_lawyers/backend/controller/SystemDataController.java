package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.services.central.SystemDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/system-data")
@RequiredArgsConstructor
public class SystemDataController {

    private final SystemDataService systemDataService;

    @GetMapping("/courts")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getCourts() {
        return systemDataService.getAllCourts();
    }

    @GetMapping("/case-types")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getCaseTypes() {
        return systemDataService.getAllCaseTypes();
    }

    @GetMapping("/subscription-plans")
    @PreAuthorize(("hasRole('SUPER_ADMIN')"))
    public ResponseEntity<?> getSubscriptionPlans() {
            return systemDataService.getSubscriptionPlans();
    }

}