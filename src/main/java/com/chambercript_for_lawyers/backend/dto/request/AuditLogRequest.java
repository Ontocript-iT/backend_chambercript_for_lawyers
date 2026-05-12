package com.chambercript_for_lawyers.backend.dto.request;

import lombok.Data;

@Data
public class AuditLogRequest {
    private String action;
    private String entityName;
    private String entityId;
    private String details;
}