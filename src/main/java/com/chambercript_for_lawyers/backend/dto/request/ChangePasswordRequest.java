package com.chambercript_for_lawyers.backend.dto.request;
import lombok.Data;
@Data
public
class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
}