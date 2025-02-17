package com.example.springboot.service.auth;

import com.example.springboot.config.security.JwtAuthentication;
import com.example.springboot.config.security.JwtUtil;
import com.example.springboot.data.entity.auth.User;
import com.example.springboot.exception.CustomException;
import com.example.springboot.service.UserService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("AuthService - 토큰 갱신 (valid token)")
    void refreshTokenWithValidToken() {
        String refreshToken = "validRefreshToken";
        Claims claims = mock(Claims.class);
        when(jwtUtil.resolveToken(refreshToken)).thenReturn(refreshToken);
        when(jwtUtil.getTokenPayload(refreshToken)).thenReturn(claims);
        when(claims.get("tokenType")).thenReturn("refresh");
        when(claims.getSubject()).thenReturn("userId");

        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";
        when(jwtUtil.generateAccessToken("userId")).thenReturn(newAccessToken);
        when(jwtUtil.generateRefreshToken("userId")).thenReturn(newRefreshToken);

        Map<String, String> tokens = authService.refreshToken(refreshToken);

        assertEquals(newAccessToken, tokens.get("access_token"));
        assertEquals(newRefreshToken, tokens.get("refresh_token"));
    }

    @Test
    @DisplayName("AuthService - 토큰 갱신 (invalid token)")
    void refreshTokenWithInvalidToken() {
        String refreshToken = "invalidRefreshToken";
        when(jwtUtil.resolveToken(refreshToken)).thenReturn(refreshToken);
//        when(jwtUtil.getTokenPayload(refreshToken)).thenThrow(new CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED));

        CustomException exception = assertThrows(CustomException.class, () -> authService.refreshToken(refreshToken));

        assertEquals("Invalid refresh token", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    @DisplayName("AuthService - 토큰 갱신 (invalid token type)")
    void refreshTokenWithInvalidTokenType() {
        String refreshToken = "invalidTokenType";
        Claims claims = mock(Claims.class);
        when(jwtUtil.resolveToken(refreshToken)).thenReturn(refreshToken);
        when(jwtUtil.getTokenPayload(refreshToken)).thenReturn(claims);
        when(claims.get("tokenType")).thenReturn("access");

        CustomException exception = assertThrows(CustomException.class, () -> authService.refreshToken(refreshToken));

        assertEquals("tokenType Error", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    @DisplayName("AuthService - get User from authentication (valid authentication)")
    void getUserFromAuthenticationWithValidAuthentication() {
        JwtAuthentication jwtAuthentication = mock(JwtAuthentication.class);
        SecurityContextHolder
                .getContext()
                .setAuthentication(jwtAuthentication);
        when(jwtAuthentication.getPrincipal()).thenReturn("userId");

        User user = new User();
        user.setId("userId");
        when(userService.getUserByUserId("userId")).thenReturn(user);

        User authenticationUser = authService.getUserFromAuthentication();

        assertEquals(user, authenticationUser);
    }

    @Test
    @DisplayName("AuthService - get User from authentication (Invalid authentication)")
    void getUserFromAuthenticationWithInvalidAuthentication() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(null);

        CustomException exception = assertThrows(CustomException.class, () -> authService.getUserFromAuthentication());

        assertEquals("Invalid authentication", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    @DisplayName("AuthService - get User from authentication (Blank UserId)")
    void getUserFromAuthenticationWithBlankUserIdAuthentication() {
        JwtAuthentication jwtAuthentication = mock(JwtAuthentication.class);
        SecurityContextHolder
                .getContext()
                .setAuthentication(jwtAuthentication);
        when(jwtAuthentication.getPrincipal()).thenReturn("");

        CustomException exception = assertThrows(CustomException.class, () -> authService.getUserFromAuthentication());

        assertEquals("Invalid authentication", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    @DisplayName("AuthService - get User from authentication (Null UserId)")
    void getUserFromAuthenticationWithNullUserIdAuthentication() {
        JwtAuthentication jwtAuthentication = mock(JwtAuthentication.class);
        SecurityContextHolder
                .getContext()
                .setAuthentication(jwtAuthentication);
        when(jwtAuthentication.getPrincipal()).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, () -> authService.getUserFromAuthentication());

        assertEquals("Invalid authentication", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    @DisplayName("AuthService - get User from authentication (User not found)")
    void getUserFromAuthenticationWithUserNotFound() {
        JwtAuthentication jwtAuthentication = mock(JwtAuthentication.class);
        SecurityContextHolder
                .getContext()
                .setAuthentication(jwtAuthentication);
        when(jwtAuthentication.getPrincipal()).thenReturn("userId");

        when(userService.getUserByUserId("userId")).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, () -> authService.getUserFromAuthentication());

        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}