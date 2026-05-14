package com.chambercript_for_lawyers.backend.services.central;

import com.chambercript_for_lawyers.backend.model.CaseType;
import com.chambercript_for_lawyers.backend.model.Court;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SystemDataService {
    ResponseEntity<?> getAllCaseTypes();

    ResponseEntity<?> getAllCourts();
}
