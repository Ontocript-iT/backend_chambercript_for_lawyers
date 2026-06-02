package com.chambercript_for_lawyers.backend.services.central;

import com.chambercript_for_lawyers.backend.model.Hearing;
import org.springframework.http.ResponseEntity;

public interface HearingService {
    ResponseEntity<?> saveHearing(Long caseId, Hearing hearingRequest);

    ResponseEntity<?> getHearingsByCase(Long caseId);
}
