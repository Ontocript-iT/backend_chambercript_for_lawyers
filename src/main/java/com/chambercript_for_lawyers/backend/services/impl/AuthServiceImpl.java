package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.dto.request.ChangePasswordRequest;
import com.chambercript_for_lawyers.backend.dto.request.RegisterAdminRequest;
import com.chambercript_for_lawyers.backend.dto.request.RegisterEmployeeRequest;
import com.chambercript_for_lawyers.backend.dto.request.SubscriptionRequest;
import com.chambercript_for_lawyers.backend.model.*;
import com.chambercript_for_lawyers.backend.repository.*;
import com.chambercript_for_lawyers.backend.security.JwtService;
import com.chambercript_for_lawyers.backend.services.BunnyNetStorageService;
import com.chambercript_for_lawyers.backend.services.SmsService;
import com.chambercript_for_lawyers.backend.services.central.AuthService;
import com.chambercript_for_lawyers.backend.services.central.EmailService;
import com.sun.security.jgss.GSSUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuditLogRepository auditLogRepository;

    private final SmsService smsService;

    private final SubscriptionPaymentRepository subscriptionPaymentRepository;

    private final SubscriptionPaymentServiceImpl subscriptionPaymentService;

    private final EmployeeRepository employeeRepository;

    private final SubscriptionServiceImpl subscriptionService;

    private final JwtService jwtUtil;

    private final EmailService emailService;

    private final SubscriptionUsageRepository subscriptionUsageRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final AdminActivityServiceImpl adminActivityService;

    private final BunnyNetStorageService bunnyNetStorageService;

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
                .registrationDate(LocalDateTime.now())
                .lawFirmCode(generatedCode)
                .verificationToken(token)
                .build();

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), token);

        SubscriptionRequest subscriptionRequest = SubscriptionRequest.builder()
                .planType(request.getPlanType())
                .smsPlan(request.getSmsPlan())
                .isActive(false)
                .build();


        if (request.getPlanType() == PlanType.CUSTOM) {
            subscriptionRequest.setCustomMaxEmployees(15);
            subscriptionRequest.setCustomMaxStorageGb(100);
        }


        ResponseEntity<?> planResponse = subscriptionService.choosePlan(user.getId(), subscriptionRequest);

        if (planResponse.getStatusCode().isError()) {
            return planResponse;
        }

        Optional<Subscription> subscription = subscriptionRepository.findByAdminId(user.getId());

        Subscription subscriptionId = subscription.orElseThrow(() -> new RuntimeException("Subscription not found for admin: " + user.getId()));

        SubscriptionUsage usage = subscriptionUsageRepository.findBySubscription(subscriptionId)
                .orElseGet(() -> SubscriptionUsage.builder()
                        .subscription(subscriptionId)
                        .usedSmsCount(0)
                        .currentEmployeesCount(0)
                        .usedStorageMb(0.0)
                        .build());

        subscriptionUsageRepository.save(usage);
        emailService.sendTempPasswordEmail(user.getEmail(), request.getPassword());

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

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());
        var currentMonthPaymentStatus = subscriptionPaymentService.checkCurrentMonthPaymentStatus(user.getLawFirmCode());
        boolean isPaymentCompleted = false;

        if (user.getRole()!=Role.SUPER_ADMIN) {
            String paymentMessage = "";
            if (currentMonthPaymentStatus != null && currentMonthPaymentStatus.getBody() != null) {
                Map<String, Object> responseBody = (Map<String, Object>) currentMonthPaymentStatus.getBody();

                if (responseBody.get("message") != null) {
                    paymentMessage = (String) responseBody.get("message");
                }
            }


          isPaymentCompleted = !paymentMessage.toLowerCase().contains("pending");
            boolean isSendSms= false;
            isSendSms=smsService.checkSmsCanSend(user.getId());

            if (isTrialExpired(user)) {
                boolean hasPaid = subscriptionPaymentRepository.existsByLawFirmCodeAndIsPaidTrue(user.getLawFirmCode());

                System.out.println("Trial expired for user: " + user.getEmail() + ". Payment status: " + (hasPaid ? "Paid" : "Not Paid"));
                if (!hasPaid) {
                    HashMap<String, Object> safeUserData = new HashMap<>();
                    safeUserData.put("id", user.getId());
                    safeUserData.put("email", user.getEmail());
                    safeUserData.put("lawFirmCode", user.getLawFirmCode());
                    safeUserData.put("isPaymentCompleted", true);
                    safeUserData.put("role", user.getRole());
                    response.put("status", 200);
                    response.put("isSendSms", isSendSms);
                    response.put("token", token);
                    response.put("user", safeUserData);
                    response.put("message", "Trial period expired. Please complete payment to continue using the service.");
                    return ResponseEntity.status(200).body(response);
                }
            }
            if (!user.isEmailVerified()) {
                response.put("status", 403);
                response.put("message", "Email not verified. Please check your inbox.");
                return ResponseEntity.status(403).body(response);
            }

        }

        HashMap<String, Object> safeUserData = new HashMap<>();
        safeUserData.put("id", user.getId());
        safeUserData.put("email", user.getEmail());
        safeUserData.put("lawFirmCode", user.getLawFirmCode());
        safeUserData.put("isPaymentCompleted", isPaymentCompleted);
        safeUserData.put("role", user.getRole());

        response.put("status", 200);
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("user", safeUserData);

        return ResponseEntity.status(200).body(response);
    }

    public boolean isTrialExpired(User user) {
        if (user == null || user.getRegistrationDate() == null) {
            return true;
        }
        long daysPassed = ChronoUnit.DAYS.between(user.getRegistrationDate(), LocalDateTime.now());

        return daysPassed <= 14;
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
    public ResponseEntity<?> registerEmployee(
            @ModelAttribute RegisterEmployeeRequest request,
            @RequestParam(value = "identityImage1", required = false) MultipartFile identityImage1,
            @RequestParam(value = "identityImage2", required = false) MultipartFile identityImage2) {

        HashMap<String, Object> response = new HashMap<>();

        try {
            // 1. Validate Roles
            Role assignedRole = Role.valueOf(request.getRole().toUpperCase());
            if (assignedRole != Role.CLERK && assignedRole != Role.JUNIOR_LAWYER) {
                response.put("status", 400);
                response.put("message", "Invalid role. Must be CLERK or JUNIOR_LAWYER.");
                return ResponseEntity.status(400).body(response);
            }

            // 2. Validate Identity Images based on Type
            if ("NIC".equalsIgnoreCase(request.getIdentifyType())) {
                if (identityImage1 == null || identityImage1.isEmpty() || identityImage2 == null || identityImage2.isEmpty()) {
                    response.put("status", 400);
                    response.put("message", "Both front and back images are required for NIC.");
                    return ResponseEntity.status(400).body(response);
                }
            } else {
                if (identityImage1 == null || identityImage1.isEmpty()) {
                    response.put("status", 400);
                    response.put("message", "An identity image is required for " + request.getIdentifyType() + ".");
                    return ResponseEntity.status(400).body(response);
                }
            }

            // 3. User & Admin Validations
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                response.put("status", 400);
                response.put("message", "Email already exists");
                return ResponseEntity.status(400).body(response);
            }

            User admin = userRepository.findById(request.getAdminId())
                    .orElseThrow(() -> new RuntimeException("Admin user not found with ID: " + request.getAdminId()));

            if (!admin.getRole().equals(Role.ADMIN)) {
                response.put("status", 403);
                response.put("message", "Only admins can register employees.");
                return ResponseEntity.status(403).body(response);
            }

            ResponseEntity<?> trackingResponse = adminActivityService.trackEmployeeCreation(admin.getId());
            if (trackingResponse.getStatusCode().isError()) {
                return trackingResponse;
            }

            // 4. Handle Bunny.net Uploads
            String bunnyFolderPath = "Employee Identification Images";
            String imageUrl1 = "";
            String imageUrl2 = "";

            // Assuming your existing BunnyNetService has a method like uploadFile(MultipartFile file, String folder, String fileName)
            String baseFileName = request.getIdentifyType() + "_" + request.getNic();

            if (identityImage1 != null && !identityImage1.isEmpty()) {
                imageUrl1 = bunnyNetStorageService.uploadFile(identityImage1, bunnyFolderPath, baseFileName + "_front");
            }

            if ("NIC".equalsIgnoreCase(request.getIdentifyType()) && identityImage2 != null && !identityImage2.isEmpty()) {
                imageUrl2 = bunnyNetStorageService.uploadFile(identityImage2, bunnyFolderPath, baseFileName + "_back");
            }

            // 5. Save Entities
            User user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .nic(request.getNic())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(assignedRole)
                    .lawFirmCode(admin.getLawFirmCode())
                    .isEmailVerified(true)
                    .build();

            userRepository.save(user);

            Employee employee = Employee.builder()
                    .userAccount(user)
                    .identifyType(request.getIdentifyType())
                    .imageUrl_1(imageUrl1) // Set the uploaded URL
                    .imageUrl_2(imageUrl2) // Set the uploaded URL (will be empty string if not NIC)
                    .admin(admin)
                    .build();

            employeeRepository.save(employee);

            emailService.sendTempPasswordEmail(user.getEmail(), request.getPassword());

            // 6. Audit Logging
            AuditLog log = AuditLog.builder()
                    .lawFirmCode(admin.getLawFirmCode())
                    .action("CREATE")
                    .actorId(admin.getId())
                    .actorName(admin.getFirstName() + " " + admin.getLastName())
                    .entityName("Employee")
                    .entityId(String.valueOf(employee.getId()))
                    .details("Created a new employee account for " + request.getEmail() + " with " + request.getIdentifyType())
                    .build();

            auditLogRepository.save(log);

            response.put("status", 201);
            response.put("message", assignedRole + " registered successfully.");

        } catch (IllegalArgumentException e) {
            response.put("status", 400);
            response.put("message", "Invalid role format.");
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "An error occurred during registration: " + e.getMessage());
            // In a production system, log the exception (e.g., log.error("...", e)) instead of throwing a generic RuntimeException
            return ResponseEntity.status(500).body(response);
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

    @Override
    public ResponseEntity<?> resetPassword(String token, String newPassword) {
        HashMap<String, Object> response = new HashMap<>();
        Optional<User> userOptional = userRepository.findByVerificationToken(token);

        if (userOptional.isEmpty()) {
            response.put("status", 400);
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(400).body(response);
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerificationToken(null);
        userRepository.save(user);

        response.put("status", 200);
        response.put("message", "Password reset successfully.");

        return ResponseEntity.status(200).body(response);
    }


}