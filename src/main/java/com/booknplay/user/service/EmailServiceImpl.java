package com.booknplay.user.service;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Override
    @Async
    public void sendRegistrationEmail(String email, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Welcome to BooknPlay");
        message.setText(
                "Hello " + name +
                        ",\n\nYour registration with BooknPlay was successful.\n\n" +
                        "Thank you for joining us!"
        );
        mailSender.send(message);
    }
    @Override
    @Async
    public void sendProfileUpdateEmail(String email, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Profile Updated - BooknPlay");
        message.setText(
                "Hello " + name +
                        ",\n\nYour BooknPlay profile has been updated successfully."
        );
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String email, String name, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request - BooknPlay");
        message.setText(
                "Hello " + name +
                        ",\n\nWe received a request to reset your password. " +
                        "Please use the following token to reset your password:\n\n" +
                        token + "\n\nIf you did not request a password reset, please ignore this email."
        );
        mailSender.send(message);
    }
}