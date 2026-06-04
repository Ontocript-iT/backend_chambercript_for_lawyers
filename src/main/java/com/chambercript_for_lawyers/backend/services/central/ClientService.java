package com.chambercript_for_lawyers.backend.services.central;

import com.chambercript_for_lawyers.backend.dto.request.ClientRegistrationRequest;
import com.chambercript_for_lawyers.backend.model.User;
import org.springframework.http.ResponseEntity;

public interface ClientService {
    ResponseEntity<?> registerClient(User user, ClientRegistrationRequest request);

    ResponseEntity<?> getClientsByLawFirmCode(String lawFirmCode,int page ,int size);

    ResponseEntity<?> getClientById(Long clientId);

    ResponseEntity<?> searchClientByNic(String nic);
}