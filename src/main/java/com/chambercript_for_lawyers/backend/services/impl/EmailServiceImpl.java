package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.services.central.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    @Value("${app.mail.sender}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Verify your account");

            String verifyLink = "https://ccriptlawyer.tech/auth/verify?token=" + token;
            String htmlContent = "<p>Please click the link below to verify your email:</p>"
                    + "<p><a href=\"" + verifyLink + "\">Verify My Account</a></p>";

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Failed to construct or send MimeMessage to: {}", to, e);

        } catch (Exception e) {
            log.error("Unexpected error occurred while sending email to: {}", to, e);
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
