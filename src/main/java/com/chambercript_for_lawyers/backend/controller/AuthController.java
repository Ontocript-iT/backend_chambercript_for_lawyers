package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.dto.request.ChangePasswordRequest;
import com.chambercript_for_lawyers.backend.dto.request.RegisterAdminRequest;
import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.repository.UserRepository;
import com.chambercript_for_lawyers.backend.services.central.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterAdminRequest request) {
        return authService.registerAdmin(request);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        return authService.verifyEmail(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody HashMap<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        return authService.login(email, password);
    }

    @PostMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,@PathVariable Long id) {

        return authService.changePassword(id, request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody HashMap<String, String> request) {
        String email = request.get("email");
        return authService.forgotPassword(email);
    }
}