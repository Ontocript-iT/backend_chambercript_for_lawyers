package com.chambercript_for_lawyers.backend.dto.request;

import lombok.Data;
@Data
public
class RegisterEmployeeRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String nic;
    private String password;
    private String role; // CLERK or MANAGER
    private Long adminId; // ID of the admin registering the employee
}