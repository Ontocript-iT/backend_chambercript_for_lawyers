package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.dto.request.ChangePasswordRequest;
import com.chambercript_for_lawyers.backend.dto.request.RegisterAdminRequest;
import com.chambercript_for_lawyers.backend.dto.request.RegisterEmployeeRequest;
import com.chambercript_for_lawyers.backend.model.AuditLog;
import com.chambercript_for_lawyers.backend.model.Employee;
import com.chambercript_for_lawyers.backend.model.Role;
import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.repository.AuditLogRepository;
import com.chambercript_for_lawyers.backend.repository.EmployeeRepository;
import com.chambercript_for_lawyers.backend.repository.UserRepository;
import com.chambercript_for_lawyers.backend.security.JwtService;
import com.chambercript_for_lawyers.backend.services.central.AuthService;
import com.chambercript_for_lawyers.backend.services.central.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuditLogRepository auditLogRepository;

    private final EmployeeRepository employeeRepository;

    private final JwtService jwtUtil;

    private final EmailService emailService;

    @Override
    public ResponseEntity<?> registerAdmin(RegisterAdminRequest request) {
        HashMap<String, Object> response = new HashMap<>();

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            response.put("status", 400);
            response.put("message", "Email already exists");


            return ResponseEntity.status(400).body(response);
        }

        String generatedCode = generateAdminCode();

        String token = UUID.randomUUID().toString();
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .nic(request.getNic())
                .role(Role.ADMIN)
                .isEmailVerified(false)
                .lawFirmCode(generatedCode)
                .verificationToken(token)
                .build();

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), token);

        response.put("status", 201);
        response.put("message", "Admin registered. Please check email to verify.");

        return ResponseEntity.status(201).body(response);
    }

    private String generateAdminCode() {
        Optional<User> lastAdmin = userRepository.findFirstByRoleOrderByIdDesc(Role.ADMIN);

        if (lastAdmin.isPresent() && lastAdmin.get().getLawFirmCode() != null) {
            String lastCode = lastAdmin.get().getLawFirmCode();

            try {
                int numericPart = Integer.parseInt(lastCode.substring(2));
                return String.format("LF%06d", numericPart + 1);

            } catch (NumberFormatException e) {
                return "LF000001";
            }
        }

        // If no admin exists in the database yet, start at 1
        return "LF000001";
    }

    @Override
    public ResponseEntity<?> login(String email, String password) {
        HashMap<String, Object> response = new HashMap<>();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty() || !passwordEncoder.matches(password, userOptional.get().getPassword())) {
            response.put("status", 401);
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }

        User user = userOptional.get();
        if (!user.isEmailVerified()) {
            response.put("status", 403);
            response.put("message", "Email not verified. Please check your inbox.");
            return ResponseEntity.status(403).body(response);
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());

        HashMap<String, Object> safeUserData = new HashMap<>();
        safeUserData.put("id", user.getId());
        safeUserData.put("email", user.getEmail());
        safeUserData.put("role", user.getRole());

        response.put("status", 200);
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("user", safeUserData);

        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<?> verifyEmail(String token) {
        HashMap<String, Object> response = new HashMap<>();
        Optional<User> userOptional = userRepository.findByVerificationToken(token);

        if (userOptional.isEmpty()) {
            response.put("status", 400);
            response.put("message", "Invalid or expired token");

            return ResponseEntity.status(400).body(response);
        }

        User user = userOptional.get();
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        response.put("status", 200);
        response.put("message", "Email verified successfully.");

        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<?> changePassword(Long id, ChangePasswordRequest request) {
        HashMap<String, Object> response = new HashMap<>();

        Optional<User> userOptional = userRepository.findById(id);

        User user = userOptional.orElse(null);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            response.put("status", 400);
            response.put("message", "Current password does not match");

            return ResponseEntity.status(400).body(response);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        response.put("status", 200);
        response.put("message", "Password changed successfully");
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<?> registerEmployee(RegisterEmployeeRequest request) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            Role assignedRole = Role.valueOf(request.getRole().toUpperCase());
            if (assignedRole != Role.CLERK && assignedRole != Role.JUNIOR_LAWYER) {
                response.put("status", 400);
                response.put("message", "Invalid role. Must be CLERK or MANAGER.");

                return ResponseEntity.status(400).body(response);
            }

            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                response.put("status", 400);
                response.put("message", "Email already exists");

                return ResponseEntity.status(400).body(response);
            }

            User admin = userRepository.findById(request.getAdminId())
                    .orElseThrow(() -> new RuntimeException("Admin user not found with ID: " + request.getAdminId()));

            User user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .nic(request.getNic())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(assignedRole)
                    .isEmailVerified(true)
                    .build();

            userRepository.save(user);

            Employee employee = Employee.builder()
                    .userAccount(user)
                    .admin(admin)
                    .build();

            employeeRepository.save(employee);

            emailService.sendTempPasswordEmail(user.getEmail(), request.getPassword());

            AuditLog log = AuditLog.builder()
                    .lawFirmCode(admin.getLawFirmCode())
                    .action("CREATE")
                    .actorId(admin.getId())
                    .actorName(admin.getFirstName()+ " " + admin.getLastName())
                    .entityName("Employee")
                    .entityId(String.valueOf(employee.getId()))
                    .details("Created a new employee account for " + request.getEmail())
                    .build();

            auditLogRepository.save(log);

            response.put("status", 201);
            response.put("message", assignedRole + " registered successfully.");

        } catch (IllegalArgumentException e) {
            response.put("status", 400);
            response.put("message", "Invalid role format.");
        }

        return ResponseEntity.status((int) response.get("status")).body(response);
    }

    @Override
    public ResponseEntity forgotPassword(String email) {
        HashMap<String, Object> response = new HashMap<>();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            response.put("status", 400);
            response.put("message", "Email not found");
            return ResponseEntity.status(400).body(response);
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), token);

        response.put("status", 200);
        response.put("message", "Password reset email sent. Please check your inbox.");

        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<?> deleteEmployee(Long employeeId) {
        HashMap<String, Object> response = new HashMap<>();

        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            response.put("status", 404);
            response.put("message", "Employee not found");
            return ResponseEntity.status(404).body(response);
        }

        Employee employee = employeeOpt.get();
        userRepository.delete(employee.getUserAccount());
        employeeRepository.delete(employee);

        response.put("status", 200);
        response.put("message", "Employee deleted successfully");
        return ResponseEntity.status(200).body(response);
    }

}