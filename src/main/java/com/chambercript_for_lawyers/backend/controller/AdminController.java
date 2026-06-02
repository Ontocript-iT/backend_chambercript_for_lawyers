package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.dto.request.RegisterEmployeeRequest;
import com.chambercript_for_lawyers.backend.services.central.AdminService;
import com.chambercript_for_lawyers.backend.services.central.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuthService authService;
    private final AdminService adminService;

    @PostMapping("/register-employee")
    public ResponseEntity<?> registerEmployee(@RequestBody RegisterEmployeeRequest request) {
       return authService.registerEmployee(request);
    }

    @PostMapping("/delete-employee/{employeeId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long employeeId) {
        return authService.deleteEmployee(employeeId);
    }

    @GetMapping("/get-all-employeesByAdminId/{adminId}")
    public ResponseEntity<?> getAllEmployeesByAdminId(@PathVariable Long adminId) {
        return adminService.getAllEmployeesByAdminId(adminId);
    }

}