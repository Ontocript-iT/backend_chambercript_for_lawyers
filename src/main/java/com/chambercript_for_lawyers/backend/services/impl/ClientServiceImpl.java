package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.dto.request.ClientRegistrationRequest;
import com.chambercript_for_lawyers.backend.model.AuditLog;
import com.chambercript_for_lawyers.backend.model.Client;
import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.repository.AuditLogRepository;
import com.chambercript_for_lawyers.backend.repository.ClientRepository;
import com.chambercript_for_lawyers.backend.services.central.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
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
            String email = (request.getEmail() != null && request.getEmail().trim().isEmpty()) ? null : request.getEmail();
            String nic = (request.getNic() != null && request.getNic().trim().isEmpty()) ? null : request.getNic();
            String phone = (request.getPhone() != null && request.getPhone().trim().isEmpty()) ? null : request.getPhone();

            if (nic != null && clientRepository.findByNic(nic).isPresent()) {
                response.put("status", 400);
                response.put("message", "A client with this NIC already exists.");
                return ResponseEntity.status(400).body(response);
            }
            if (phone != null && clientRepository.findByPhone(phone).isPresent()) {
                response.put("status", 400);
                response.put("message", "A client with this Phone Number already exists.");
                return ResponseEntity.status(400).body(response);
            }
            if (email != null && clientRepository.findByEmail(email).isPresent()) {
                response.put("status", 400);
                response.put("message", "A client with this Email already exists.");
                return ResponseEntity.status(400).body(response);
            }

            Client client = Client.builder()
                    .name(request.getName())
                    .lawFirmCode(user.getLawFirmCode())
                    .nic(nic)
                    .phone(phone)
                    .email(email)
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
                    .entityId(client.getNic() != null ? client.getNic() : String.valueOf(client.getId())) // Fallback to ID if NIC is null
                    .details("Registered new client: " + client.getName())
                    .build();

            auditLogRepository.save(auditLog);

            response.put("status", 201);
            response.put("id", client.getId());
            response.put("message", "Client registered successfully.");

            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Failed to register client: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Override
    public ResponseEntity<?> getClientsByLawFirmCode(String lawFirmCode, int page, int size) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Pageable pageable = PageRequest.of(page, size);


            Page<Client> clientPage = clientRepository.findByLawFirmCode(lawFirmCode, pageable);
            List<Client> clients = clientPage.getContent();

            if (clients.isEmpty()) {
                response.put("status", 404);
                response.put("message", "No clients found for the provided chamber.");
                return ResponseEntity.status(404).body(response);
            }

            response.put("status", 200);
            response.put("message", "Clients retrieved successfully.");
            response.put("data", clients);

            response.put("currentPage", clientPage.getNumber());
            response.put("totalItems", clientPage.getTotalElements());
            response.put("totalPages", clientPage.getTotalPages());
            response.put("pageSize", clientPage.getSize());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "An error occurred while retrieving clients: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Override
    public ResponseEntity<?> getClientById(Long id) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Optional<Client> clientOpt = clientRepository.findById(id);

            if (clientOpt.isEmpty()) {
                response.put("status", 404);
                response.put("message", "Client not found with the provided ID.");
                return ResponseEntity.status(404).body(response);
            }

            response.put("status", 200);
            response.put("message", "Client retrieved successfully.");
            response.put("data", clientOpt.get());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "An error occurred while retrieving the client: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Override
    public ResponseEntity<?> searchClientByNic(String nic) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Optional<Client> clientOpt = clientRepository.findByNic(nic);

            if (clientOpt.isEmpty()) {
                response.put("status", 404);
                response.put("message", "Client not found with the provided NIC.");
                return ResponseEntity.status(404).body(response);
            }

            response.put("status", 200);
            response.put("message", "Client retrieved successfully.");
            response.put("data", clientOpt.get());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "An error occurred while retrieving the client: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}
