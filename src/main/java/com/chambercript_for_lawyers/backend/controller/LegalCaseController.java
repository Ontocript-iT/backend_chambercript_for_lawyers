package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.dto.request.CaseRegistrationRequest;
import com.chambercript_for_lawyers.backend.services.central.LegalCaseService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class LegalCaseController {

    private final LegalCaseService legalCaseService;

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> registerCase(@RequestBody CaseRegistrationRequest dto) {
            return legalCaseService.registerNewCase(dto);

    }
}