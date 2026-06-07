package com.chambercript_for_lawyers.backend.dto.request;

import com.chambercript_for_lawyers.backend.enums.SmsPlan;
import com.chambercript_for_lawyers.backend.model.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {
    private PlanType planType;

    // These are only required if planType == CUSTOM
    private Integer customMaxEmployees;
    private Integer customMaxStorageGb;
    private Boolean isActive;
    private SmsPlan smsPlan;

    private String smsPlanType;
    private Integer smsQuota;


}