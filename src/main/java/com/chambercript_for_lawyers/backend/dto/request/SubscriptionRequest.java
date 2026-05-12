package com.chambercript_for_lawyers.backend.dto.request;

import com.chambercript_for_lawyers.backend.model.PlanType;
import lombok.Data;

@Data
public class SubscriptionRequest {
    private PlanType planType;

    // These are only required if planType == CUSTOM
    private Integer customMaxEmployees;
    private Integer customMaxStorageGb;
}