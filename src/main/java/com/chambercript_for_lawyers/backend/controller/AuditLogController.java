package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.dto.request.AuditLogRequest;
import com.chambercript_for_lawyers.backend.services.central.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getLogsByLawFirmCode(@PathVariable String lawFirmCode,@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return auditLogService.getLogsByLawFirmCode(lawFirmCode,page, size);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllLogs() {
        return auditLogService.getAllLogs();
    }
}