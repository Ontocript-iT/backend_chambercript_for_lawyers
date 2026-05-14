package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.dto.request.ClientRegistrationRequest;
import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.repository.UserRepository;
import com.chambercript_for_lawyers.backend.services.central.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> registerClient(Principal principal, @RequestBody ClientRegistrationRequest request) {

        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized: No active session.");
        }

        String creatorLawFirmCode = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getLawFirmCode();

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return clientService.registerClient(user, request);
    }
}