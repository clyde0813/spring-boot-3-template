package com.example.springboot.data.dto.auth.request;

import lombok.Data;

@Data
public class SignUpDto {
    private String username;
    private String password;
}
