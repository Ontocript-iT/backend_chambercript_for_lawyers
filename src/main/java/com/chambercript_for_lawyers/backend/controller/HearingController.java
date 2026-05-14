package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.model.Hearing;
import com.chambercript_for_lawyers.backend.services.central.HearingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/hearings")
@RequiredArgsConstructor
public class HearingController {

    private final HearingService hearingService;


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
}