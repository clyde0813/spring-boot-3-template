package com.example.springboot.service.auth;

import com.example.springboot.config.security.JwtAuthentication;
import com.example.springboot.config.security.JwtUtil;
import com.example.springboot.data.entity.auth.User;
import com.example.springboot.exception.CustomException;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    private final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    public Map<String, String> refreshToken(String refreshToken) {
        try {
            // refresh token - Bearer 제거후 payload 확인
            Claims claims = jwtUtil.getTokenPayload(jwtUtil.resolveToken(refreshToken));
            if (!"refresh".equals(claims.get("tokenType"))) {
                LOGGER.error("[getUserFromToken] Token tokenType mismatch");
                throw new CustomException("tokenType Error", HttpStatus.BAD_REQUEST);
            }
            String newAccessToken = jwtUtil.generateAccessToken(claims.getSubject());
            String newRefreshToken = jwtUtil.generateRefreshToken(claims.getSubject());
            return Map.of("access_token", newAccessToken, "refresh_token", newRefreshToken);
        } catch (Exception e) {
            throw new CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }
    }

    public User getUserFromAuthentication() {
        JwtAuthentication jwtAuthentication = (JwtAuthentication) SecurityContextHolder
                .getContext()
                .getAuthentication();
        String userId = jwtAuthentication.getPrincipal();
        return userService.getUserByUserId(userId);
    }
}