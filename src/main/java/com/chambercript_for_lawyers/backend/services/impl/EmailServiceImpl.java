package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.services.central.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    @Value("${app.mail.sender}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Verify your account");
            message.setText("Please click the link to verify your email: " +
                    "http://localhost:8080/api/auth/verify?token=" + token);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }

    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail); // Uses the same verified Brevo sender
            message.setTo(to);
            message.setSubject("Password Reset Request");

            // Note: In a production app, this link usually points to your frontend
            // application (like React, Angular, Flutter, etc.) which then sends the
            // new password and token back to your Spring Boot API.
            message.setText("You have requested to reset your password.\n\n" +
                    "Please click the link below to set a new password:\n" +
                    "https://ccriptlawyer.tech/auth/reset-password?token=" + token + "\n\n" +
                    "If you did not request this, please ignore this email.");

            mailSender.send(message);
        }catch (Exception e){
                System.err.println("Error sending email: " + e.getMessage());
        }

    }

    @Override
    public void sendTempPasswordEmail(String email, String password) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Your Temporary Password");
            message.setText("Your password has been reset. Your temporary password is: " + password +
                    "\n\nPlease log in and change your password immediately.");

            mailSender.send(message);
        }catch (Exception e){
            System.err.println("Error sending email: " + e.getMessage());
        }

    }
}
