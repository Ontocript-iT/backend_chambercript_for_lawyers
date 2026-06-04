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

    @GetMapping("/getClientsByLawFirmCode/{lawFirmCode}" )
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getClientsByLawFirmCode(@PathVariable String lawFirmCode,@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return clientService.getClientsByLawFirmCode(lawFirmCode,page, size);
    }


    @GetMapping("/getClientById/{clientId}" )
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getClientById(@PathVariable Long clientId) {
        return clientService.getClientById(clientId);
    }

    @PostMapping("/searchClientByNin/{nic}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> searchClientByNic(@PathVariable String nic) {
        return clientService.searchClientByNic(nic);
    }
}