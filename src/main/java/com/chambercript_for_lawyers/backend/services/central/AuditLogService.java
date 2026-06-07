package com.chambercript_for_lawyers.backend.services.central;

import com.chambercript_for_lawyers.backend.dto.request.AuditLogRequest;
import org.springframework.http.ResponseEntity;

public interface AuditLogService {
    ResponseEntity<?> logAction(String LawFirmCode, AuditLogRequest request);
    ResponseEntity<?> getLogsByLawFirmCode(String LawFirmCode,int page, int size);
    ResponseEntity<?> getAllLogs();
}