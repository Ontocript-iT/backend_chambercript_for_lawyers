package com.chambercript_for_lawyers.backend.dto.request;

import lombok.Data;

@Data
public class ClientRegistrationRequest {
    private String name;
    private String nic;
    private String password;
    private String phone;
    private String email;
    private String address;
    private String notes;
}