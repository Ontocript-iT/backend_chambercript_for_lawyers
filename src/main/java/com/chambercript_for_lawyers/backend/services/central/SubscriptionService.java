package com.chambercript_for_lawyers.backend.services.central;

import com.chambercript_for_lawyers.backend.dto.request.SubscriptionRequest;
import org.springframework.http.ResponseEntity;

public interface SubscriptionService {
    ResponseEntity<?> choosePlan(Long adminId, SubscriptionRequest request);

    ResponseEntity<?> updatePlan(Long adminId, SubscriptionRequest request);
}
