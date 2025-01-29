package com.example.springboot.service.auth;

import com.example.springboot.config.security.JwtUtil;
import com.example.springboot.data.dto.auth.request.LoginDto;
import com.example.springboot.data.dto.auth.request.SignUpDto;
import com.example.springboot.data.entity.auth.User;
import com.example.springboot.exception.CustomException;
import com.example.springboot.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService{
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final JwtUtil jwtUtil;

    public Boolean signUp(SignUpDto signUpDto) {
        String encodedPassword = passwordEncoderService.encode(signUpDto.getPassword());
        User user = new User();
        user.setUsername(signUpDto.getUsername());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return true;
    }

    public Map<String, String> login(LoginDto loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty() || !passwordEncoderService.matches(password, user.get().getPassword())) {
            throw new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
        String accessToken = jwtUtil.generateAccessToken(user.get().getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.get().getId());
        return Map.of("access_token", accessToken, "refresh_token", refreshToken);
    }

    public Map<String, String> refreshToken(String refreshToken) {
        try {
            Claims claims = jwtUtil.validateToken(refreshToken);
            String newAccessToken = jwtUtil.generateAccessToken(claims.getSubject());
            String newRefreshToken = jwtUtil.generateRefreshToken(claims.getSubject());
            return Map.of("access_token", newAccessToken, "refresh_token", newRefreshToken);
        } catch (Exception e) {
            throw new CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }
    }

    public User getUserByAccessToken(String accessToken) {
        return jwtUtil.getUserFromToken(accessToken);
    }
}
