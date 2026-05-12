package com.chambercript_for_lawyers.backend.services.central;

public interface EmailService {
    void sendVerificationEmail(String to, String token);
    void sendPasswordResetEmail(String to, String token);

    void sendTempPasswordEmail(String email, String password);
}