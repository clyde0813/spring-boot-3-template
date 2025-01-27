package com.example.springboot.service.auth;

import com.example.springboot.data.dto.auth.request.LoginDto;
import com.example.springboot.data.dto.auth.request.SignUpDto;

import java.util.Map;

public interface AuthService {
    Boolean signUp(SignUpDto signUpDto);

    Map<String, String> login(LoginDto loginDto);
}
