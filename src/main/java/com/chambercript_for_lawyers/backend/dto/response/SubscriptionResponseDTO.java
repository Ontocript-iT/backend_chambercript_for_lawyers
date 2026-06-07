package com.chambercript_for_lawyers.backend.dto.response;

import com.chambercript_for_lawyers.backend.enums.SmsPlan;
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
    private boolean isActive;
    private SmsPlan smsPlan;

    // Add to your existing SubscriptionResponseDTO
    private String smsPlanType;
    private Integer smsQuota;

}