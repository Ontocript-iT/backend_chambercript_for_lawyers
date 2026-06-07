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
    private String role;
    private Long adminId;
    private String phoneNumber;
    private String identifyType;
    private String imageUrl_1;
    private String imageUrl_2;
}