package com.chambercript_for_lawyers.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponse {
    private Long id;
    private String lawFirmCode;
    private String action;
    private String entityName;
    private String entityId;
    private String details;
    private LocalDateTime timestamp;
}