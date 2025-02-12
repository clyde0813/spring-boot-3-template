package com.example.springboot.controller.auth;

import com.example.springboot.config.security.JwtUtil;
import com.example.springboot.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // 토큰 Refresh
    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestHeader(value = "refresh", required = false) String refreshToken
    ) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    // 토큰 검증
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(
            @RequestParam String token
    ) {
        jwtUtil.validateToken(token);
        return ResponseEntity.ok(true);
    }
}
