package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.services.central.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/{userId}/upload-profile-picture", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> uploadProfilePicture(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
           return userService.uploadProfilePicture(userId, file);

    }

    @GetMapping("/getUserDetailsById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getUserDetailsById(@PathVariable Long id) {
        return userService.getUserDetailsById(id);
    }
}