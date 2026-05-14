package com.chambercript_for_lawyers.backend.services.central;

import com.chambercript_for_lawyers.backend.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    ResponseEntity<?> uploadProfilePicture(Long userId, MultipartFile file);
}
