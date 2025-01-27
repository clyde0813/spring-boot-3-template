package com.example.springboot.service.auth;

public interface PasswordEncoderService {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
