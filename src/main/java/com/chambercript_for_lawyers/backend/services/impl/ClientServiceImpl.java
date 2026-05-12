package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.dto.request.ClientRegistrationRequest;
import com.chambercript_for_lawyers.backend.model.AuditLog;
import com.chambercript_for_lawyers.backend.model.Client;
import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.repository.AuditLogRepository;
import com.chambercript_for_lawyers.backend.repository.ClientRepository;
import com.chambercript_for_lawyers.backend.services.central.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public ResponseEntity<?> registerClient(User user, ClientRegistrationRequest request) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            if (clientRepository.findByNic(request.getNic()).isPresent()) {
                response.put("status", 400);
                response.put("message", "A client with this NIC already exists.");
                return ResponseEntity.status(400).body(response);
            }
            if (clientRepository.findByPhone(request.getPhone()).isPresent()) {
                response.put("status", 400);
                response.put("message", "A client with this Phone Number already exists.");
                return ResponseEntity.status(400).body(response);
            }
            if (request.getEmail() != null && !request.getEmail().isEmpty() && clientRepository.findByEmail(request.getEmail()).isPresent()) {
                response.put("status", 400);
                response.put("message", "A client with this Email already exists.");
                return ResponseEntity.status(400).body(response);
            }

            Client client = Client.builder()
                    .name(request.getName())
                    .lawFirmCode(user.getLawFirmCode())
                    .nic(request.getNic())
                    .phone(request.getPhone())
                    .email(request.getEmail())
                    .address(request.getAddress())
                    .notes(request.getNotes())
                    .build();

            client = clientRepository.save(client);

            AuditLog auditLog = AuditLog.builder()
                    .lawFirmCode(user.getLawFirmCode())
                    .action("CREATE")
                    .actorName(user.getFirstName() + " " + user.getLastName())
                    .actorId(user.getId())
                    .entityName("Client")
                    .entityId(client.getNic())
                    .details("Registered new client: " + client.getName() + " (NIC: " + client.getNic() + ")")
                    .build();

            auditLogRepository.save(auditLog);

            response.put("status", 201);
            response.put("message", "Client registered successfully.");

            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "An error occurred while registering the client: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}
