package com.chambercript_for_lawyers.backend.services.central;


import com.chambercript_for_lawyers.backend.dto.request.ChangePasswordRequest;
import com.chambercript_for_lawyers.backend.dto.request.RegisterAdminRequest;
import com.chambercript_for_lawyers.backend.dto.request.RegisterEmployeeRequest;
import com.chambercript_for_lawyers.backend.model.User;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public interface AuthService {
    ResponseEntity<?> registerAdmin(RegisterAdminRequest request);
    ResponseEntity<?> verifyEmail(String token);
    ResponseEntity<?> changePassword(Long id, ChangePasswordRequest request);
    ResponseEntity<?> registerEmployee(RegisterEmployeeRequest request);

    ResponseEntity<?> login(String email, String password);

    ResponseEntity<?> forgotPassword(String email);

    ResponseEntity<?> deleteEmployee(Long employeeId);
}