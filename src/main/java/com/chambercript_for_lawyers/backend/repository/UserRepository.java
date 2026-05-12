package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.Role;
import com.chambercript_for_lawyers.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);

    Optional<User> findFirstByRoleOrderByIdDesc(Role role);
}