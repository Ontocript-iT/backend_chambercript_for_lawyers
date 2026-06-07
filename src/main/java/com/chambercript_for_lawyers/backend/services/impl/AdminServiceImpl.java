package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.model.Employee;
import com.chambercript_for_lawyers.backend.repository.EmployeeRepository;
import com.chambercript_for_lawyers.backend.services.central.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final EmployeeRepository employeeRepository;

    @Override
    public ResponseEntity<?> getAllEmployeesByAdminId(Long adminId) {
        try {
            List<Employee> employees = employeeRepository.findByAdminId(adminId);

            if (employees.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", 404);
                errorResponse.put("message", "No employees found for admin ID: " + adminId);
                return ResponseEntity.status(404).body(errorResponse);
            }

            List<Map<String, Object>> cleanEmployeeData = employees.stream().map(emp -> {
                Map<String, Object> empMap = new HashMap<>();

                empMap.put("id", emp.getId());

                if (emp.getUserAccount() != null) {
                    String fullName = emp.getUserAccount().getFirstName() + " " + emp.getUserAccount().getLastName();
                    empMap.put("name", fullName);
                    empMap.put("email", emp.getUserAccount().getEmail());
                    empMap.put("phone", emp.getUserAccount().getPhone());
                    empMap.put("image1", emp.getImageUrl_1());
                    empMap.put("image2", emp.getImageUrl_2());
                    empMap.put("identificationNumber", emp.getUserAccount().getNic());
                    empMap.put("identifyType", emp.getIdentifyType());
                    empMap.put("role", emp.getUserAccount().getRole());
                    empMap.put("userId", emp.getUserAccount().getId());
                    empMap.put("profilePictureUrl", emp.getUserAccount().getProfilePictureUrl());
                }

                return empMap;
            }).collect(Collectors.toList());

            HashMap<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Employees retrieved successfully.");
            response.put("data", cleanEmployeeData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "Error retrieving employees: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
