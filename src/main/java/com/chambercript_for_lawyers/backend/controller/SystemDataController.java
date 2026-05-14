package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.services.central.SystemDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system-data")
@RequiredArgsConstructor
public class SystemDataController {

    private final SystemDataService systemDataService;

    @GetMapping("/courts")
    public ResponseEntity<?> getCourts() {
        return systemDataService.getAllCourts();
    }

    @GetMapping("/case-types")
    public ResponseEntity<?> getCaseTypes() {
           return systemDataService.getAllCaseTypes();
    }
}