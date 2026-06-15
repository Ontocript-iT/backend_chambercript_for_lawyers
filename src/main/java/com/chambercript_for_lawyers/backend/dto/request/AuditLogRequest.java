package com.chambercript_for_lawyers.backend.dto.request;

import lombok.Data;

@Data
public class AuditLogRequest {
    private String action;
    private Long actorId;
    private String entityName;
    private String actorName;
    private String entityId;
    private String details;
}