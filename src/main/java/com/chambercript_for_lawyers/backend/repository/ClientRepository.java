package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByNic(String nic);
    Optional<Client> findByPhone(String phone);
    Optional<Client> findByEmail(String email);

    Optional<Client> findFirstByOrderByIdDesc();
}