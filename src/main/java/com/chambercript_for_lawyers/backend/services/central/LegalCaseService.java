package com.chambercript_for_lawyers.backend.services.central;

import com.chambercript_for_lawyers.backend.dto.request.CaseRegistrationRequest;
import com.chambercript_for_lawyers.backend.model.LegalCase;
import org.springframework.http.ResponseEntity;

public interface LegalCaseService {
    ResponseEntity<?> registerNewCase(CaseRegistrationRequest dto);
}
