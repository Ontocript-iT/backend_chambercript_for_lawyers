package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.dto.request.CaseRegistrationRequest;
import com.chambercript_for_lawyers.backend.enums.CaseStatus;
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
    public ResponseEntity<?> getFutureCases(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return legalCaseService.getFutureCases(page, size);
    }

    @PutMapping("/updateCase/{caseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> updateCase(@RequestBody CaseRegistrationRequest dto,@PathVariable  Long caseId) {
        return legalCaseService.updateCase(dto,caseId);
    }

    @DeleteMapping("/deleteCase/{caseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> deleteCase(@PathVariable Long caseId) {
        return legalCaseService.deleteCase(caseId);
    }

    @GetMapping("/getCaseTypes")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getCaseTypes() {
        return legalCaseService.getCaseTypes();
    }

    @GetMapping("/getAllCourts")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getAllCourts() {
        return legalCaseService.getAllCourts();
    }

    @PutMapping("/updateCaseStatus/{caseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCaseStatus(@PathVariable Long caseId, @RequestParam CaseStatus status) {
        return legalCaseService.updateCaseStatus(caseId, status);
    }

    @GetMapping("/getCasesByStatus/{lawFirmCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCasesByStatus(@PathVariable String lawFirmCode,@RequestParam CaseStatus status,@RequestParam(defaultValue = "0") int page,    // Default to first page
                                              @RequestParam(defaultValue = "10") int size){
        return legalCaseService.getCasesByStatus(status,lawFirmCode,page,size);
    }

}