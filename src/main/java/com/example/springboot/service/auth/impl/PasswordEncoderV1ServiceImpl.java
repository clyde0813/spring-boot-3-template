package com.example.springboot.service.auth.impl;

import com.example.springboot.service.auth.PasswordEncoderService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoderV1ServiceImpl implements PasswordEncoderService {
    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordEncoderV1ServiceImpl() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
