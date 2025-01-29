package com.example.springboot.controller.auth;

import com.example.springboot.config.security.JwtUtil;
import com.example.springboot.data.dto.auth.request.LoginDto;
import com.example.springboot.data.dto.auth.request.SignUpDto;
import com.example.springboot.data.entity.auth.User;
import com.example.springboot.service.auth.AuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody SignUpDto signUpDto) {
        authService.signUp(signUpDto);
        return ResponseEntity.ok("User registered successfully");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }

    // 토큰 Refresh
    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestParam String refreshToken
    ) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    // 토큰 검증
    @GetMapping("/validate")
    public ResponseEntity<Claims> validate(
            @RequestParam String token
    ) {
        return ResponseEntity.ok(jwtUtil.validateToken(token));
    }

//    // 토큰 - 사용자 추출
//    @GetMapping("/token")
//    public ResponseEntity<?> getUser(
//            @RequestParam String token
//    ) {
//        return ResponseEntity.ok(authService.getUserByAccessToken(token));
//    }
}
