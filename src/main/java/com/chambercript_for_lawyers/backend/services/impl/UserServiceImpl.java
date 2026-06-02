package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.repository.UserRepository;
import com.chambercript_for_lawyers.backend.services.BunnyNetStorageService;
import com.chambercript_for_lawyers.backend.services.central.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BunnyNetStorageService bunnyNetStorageService;

    public ResponseEntity<?> uploadProfilePicture(Long userId, MultipartFile file) {

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            String folderPath = String.format("System_Avatars/User_%d", userId);

            String pictureUrl = bunnyNetStorageService.uploadFile(file, folderPath);

            user.setProfilePictureUrl(pictureUrl);

            userRepository.save(user);

            HashMap<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Profile picture uploaded successfully");
            response.put("pictureUrl", pictureUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading profile picture: " + e.getMessage());
        }


    }

    @Override
    public ResponseEntity<?> getUserDetailsById(Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

            HashMap<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "User details retrieved successfully");
            response.put("data", user);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error retrieving user details: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getAllLawFirms() {
        try {
            var lawFirms = userRepository.findAll();

            HashMap<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Law firms retrieved successfully");
            response.put("data", lawFirms);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving law firms: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> searchLawFirmsByLawFirmCode(String lawFirmCode) {
        try {
            Optional<User> lawFirms = userRepository.findByLawFirmCodeContainingIgnoreCase(lawFirmCode);

            HashMap<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Law firms retrieved successfully");
            response.put("data", lawFirms);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error searching law firms: " + e.getMessage());
        }
    }
}