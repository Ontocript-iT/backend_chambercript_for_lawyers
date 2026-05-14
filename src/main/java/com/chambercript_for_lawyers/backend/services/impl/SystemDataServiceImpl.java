package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.model.CaseType;
import com.chambercript_for_lawyers.backend.model.Court;
import com.chambercript_for_lawyers.backend.repository.CaseTypeRepository;
import com.chambercript_for_lawyers.backend.repository.CourtRepository;
import com.chambercript_for_lawyers.backend.services.central.SystemDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemDataServiceImpl implements SystemDataService {


    private final CourtRepository courtRepository;


    private final CaseTypeRepository caseTypeRepository;

    public ResponseEntity<?> getAllCourts() {
            try {
                List<Court> courts = courtRepository.findAll();
                HashMap response = new HashMap<>();
                response.put("status", "success");
                response.put("data", courts);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Error fetching courts: " + e.getMessage());
            }
    }

    public ResponseEntity<?> getAllCaseTypes() {
        try {
            List<CaseType> caseTypes = caseTypeRepository.findAll();
            HashMap response = new HashMap<>();
            response.put("status", "success");
            response.put("data", caseTypes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching case types: " + e.getMessage());
        }
    }
}
