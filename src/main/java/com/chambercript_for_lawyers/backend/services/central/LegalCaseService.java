package com.chambercript_for_lawyers.backend.services.central;

import com.chambercript_for_lawyers.backend.dto.request.CaseRegistrationRequest;
import com.chambercript_for_lawyers.backend.enums.CaseStatus;
import com.chambercript_for_lawyers.backend.model.LegalCase;
import org.springframework.http.ResponseEntity;

public interface LegalCaseService {
    ResponseEntity<?> registerNewCase(CaseRegistrationRequest dto);

    ResponseEntity<?> getCasesByLawFirmCode(String code);

    ResponseEntity<?> getCasesByClientId(Long clientId);

    ResponseEntity<?> getCaseById(Long caseId);

    ResponseEntity<?> getFutureCases(int page, int size);

    ResponseEntity<?> updateCase(CaseRegistrationRequest dto,Long caseId);

    ResponseEntity<?> deleteCase(Long caseId);

    ResponseEntity<?> getCaseTypes();

    ResponseEntity<?> getAllCourts();

    ResponseEntity<?> updateCaseStatus(Long caseId, CaseStatus status);

    ResponseEntity<?> getCasesByStatus(CaseStatus status,String lawFirmCode,int page,int size);
}
