package com.chambercript_for_lawyers.backend.config;

import com.chambercript_for_lawyers.backend.model.Role;
import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("superadmin@system.com").isEmpty()) {
            User superAdmin = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("superadmin@system.com")
                    .password(passwordEncoder.encode("SuperSecret123!"))
                    .registrationDate(java.time.LocalDateTime.now())
                    .role(Role.SUPER_ADMIN)
                    .isEmailVerified(true)
                    .build();
            userRepository.save(superAdmin);
            System.out.println("Super Admin initialized successfully.");
        }
    }
}