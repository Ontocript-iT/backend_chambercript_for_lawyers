package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.dto.request.AuditLogRequest;
import com.chambercript_for_lawyers.backend.dto.response.AuditLogResponse;
import com.chambercript_for_lawyers.backend.model.AuditLog;
import com.chambercript_for_lawyers.backend.repository.AuditLogRepository;
import com.chambercript_for_lawyers.backend.services.central.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public ResponseEntity<?> logAction(String lawFirmCode, AuditLogRequest request) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            AuditLog auditLog = AuditLog.builder()
                    .lawFirmCode(lawFirmCode)
                    .action(request.getAction())
                    .actorName(request.getActorName())
                    .actorId(request.getActorId())
                    .entityName(request.getEntityName())
                    .entityId(request.getEntityId())
                    .details(request.getDetails())
                    .build();

            auditLogRepository.save(auditLog);

            response.put("status", 201);
            response.put("message", "Audit log created successfully.");
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Failed to save audit log: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Override
    public ResponseEntity<?> getLogsByLawFirmCode(String lawFirmCode, int page, int size) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AuditLog> logPage = auditLogRepository.findByLawFirmCodeOrderByTimestampDesc(lawFirmCode, pageable);

            if (logPage.isEmpty()) {
                response.put("status", 404);
                response.put("message", "No audit logs found for user code: " + lawFirmCode);
                return ResponseEntity.status(404).body(response);
            }

            List<AuditLogResponse> logResponses = logPage.getContent().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            response.put("status", 200);
            response.put("message", "Audit logs retrieved successfully.");
            response.put("data", logResponses);

            response.put("currentPage", logPage.getNumber());
            response.put("totalItems", logPage.getTotalElements());
            response.put("totalPages", logPage.getTotalPages());
            response.put("pageSize", logPage.getSize());

            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Error retrieving audit logs: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


    @Override
    public ResponseEntity<?> getAllLogs() {
        HashMap<String, Object> response = new HashMap<>();

        try {
            List<AuditLogResponse> logResponses = auditLogRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            response.put("status", 200);
            response.put("data", logResponses);
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Error retrieving audit logs: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .lawFirmCode(log.getLawFirmCode())
                .action(log.getAction())
                .entityName(log.getEntityName())
                .entityId(log.getEntityId())
                .performedBy(log.getActorName())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
    }
}
