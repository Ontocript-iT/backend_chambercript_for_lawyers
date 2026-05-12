package com.chambercript_for_lawyers.backend.dto.request;

import lombok.Data;

@Data
public
class RegisterAdminRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String nic;
}