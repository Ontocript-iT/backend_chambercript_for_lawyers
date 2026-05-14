package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.model.Hearing;
import com.chambercript_for_lawyers.backend.model.LegalCase;
import com.chambercript_for_lawyers.backend.repository.HearingRepository;
import com.chambercript_for_lawyers.backend.repository.LegalCaseRepository;
import com.chambercript_for_lawyers.backend.services.central.HearingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class HearingServiceImpl implements HearingService {

    private final LegalCaseRepository legalCaseRepository;

    private final HearingRepository hearingRepository;

    @Override
    public ResponseEntity<?> saveHearing(Long caseId, Hearing hearingRequest) {

        try{
            LegalCase legalCase = legalCaseRepository.findById(caseId)
                    .orElseThrow(() -> new RuntimeException("Case not found"));

            hearingRequest.setLegalCase(legalCase);
            Hearing savedHearing = hearingRepository.save(hearingRequest);

            HashMap response = new HashMap<>();

            response.put("status", "success");
            response.put("message", "Hearing date added successfully");
            response.put("data", savedHearing);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch(Exception e){
            HashMap response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<?> getHearingsByCase(Long caseId) {
        try{
            LegalCase legalCase = legalCaseRepository.findById(caseId)
                    .orElseThrow(() -> new RuntimeException("Case not found"));

            HashMap response = new HashMap<>();

            response.put("status", "success");
            response.put("message", "Hearing dates retrieved successfully");
            response.put("data", hearingRepository.findByLegalCaseId(legalCase.getId()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch(Exception e){
            HashMap response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
