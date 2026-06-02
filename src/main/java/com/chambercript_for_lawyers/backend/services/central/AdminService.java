package com.chambercript_for_lawyers.backend.services.central;

import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<?> getAllEmployeesByAdminId(Long adminId);
}
