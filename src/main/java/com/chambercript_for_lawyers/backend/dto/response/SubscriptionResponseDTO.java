package com.chambercript_for_lawyers.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionResponseDTO {
    private Long id;
    private String planType;
    private Integer maxEmployees;
    private Integer maxStorageGb;
    private Long adminId;
    private String adminName;
}