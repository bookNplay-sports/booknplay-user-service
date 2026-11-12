package com.booknplay.user.service;

public interface EmailService {
    void sendRegistrationEmail(String email, String name);
    void sendProfileUpdateEmail(String email, String name);
    void sendPasswordResetEmail(String email, String name, String token);
}
