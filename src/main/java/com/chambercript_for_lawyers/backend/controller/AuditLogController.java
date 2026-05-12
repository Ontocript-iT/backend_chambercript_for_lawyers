package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.dto.request.AuditLogRequest;
import com.chambercript_for_lawyers.backend.services.central.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PostMapping("/{userCode}")
    public ResponseEntity<?> createLog(@PathVariable String lawFirmCode, @RequestBody AuditLogRequest request) {
        return auditLogService.logAction(lawFirmCode, request);
    }

    @GetMapping("/user/{lawFirmCode}")
    public ResponseEntity<?> getLogsByUserCode(@PathVariable String lawFirmCode) {
        return auditLogService.getLogsByLawFirmCode(lawFirmCode);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllLogs() {
        return auditLogService.getAllLogs();
    }
}