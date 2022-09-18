package com.example.UserLoginRegistration.email.repository;

public interface EmailSender {
    void send(String to, String email);
}
