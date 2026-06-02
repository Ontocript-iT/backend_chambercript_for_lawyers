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

    @GetMapping("/getCasesByLawFirmCode/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getCasesByLawFirmCode(@PathVariable String code) {
        return legalCaseService.getCasesByLawFirmCode(code);
    }

    @GetMapping("/getCaseByClientId/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getCaseByClientId(@PathVariable Long clientId) {
        return legalCaseService.getCasesByClientId(clientId);
    }

    @GetMapping("/getCaseById/{caseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getCaseById(@PathVariable Long caseId) {
        return legalCaseService.getCaseById(caseId);
    }

    @GetMapping("/getFutureCases")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getFutureCases() {
        return legalCaseService.getFutureCases();
    }


}