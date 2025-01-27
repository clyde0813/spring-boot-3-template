package com.example.springboot.data.dto.auth.request;

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
}
