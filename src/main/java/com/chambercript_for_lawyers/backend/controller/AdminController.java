package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.dto.request.RegisterEmployeeRequest;
import com.chambercript_for_lawyers.backend.services.central.AdminService;
import com.chambercript_for_lawyers.backend.services.central.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuthService authService;
    private final AdminService adminService;

    @PostMapping(value = "/register-employee", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerEmployee(
            @ModelAttribute RegisterEmployeeRequest request,
            @RequestParam(value = "identityImage1", required = false) MultipartFile identityImage1,
            @RequestParam(value = "identityImage2", required = false) MultipartFile identityImage2) {

        return authService.registerEmployee(request, identityImage1, identityImage2);
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