package com.example.springboot.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);
//    private final UserDetailsService userDetailsService;

    private final SecretKey SECRET_KEY;
    private final long ACCESS_TOKEN_EXPIRATION_TIME;
    private final long REFRESH_TOKEN_EXPIRATION_TIME;

    @Autowired
    public JwtUtil(@Value("${jwt.secret.key}") String secretKey, @Value("${jwt.access.expiration}") long accessExpiration, @Value("${jwt.refresh.expiration}") long refreshExpiration) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.ACCESS_TOKEN_EXPIRATION_TIME = accessExpiration;
        this.REFRESH_TOKEN_EXPIRATION_TIME = refreshExpiration;
    }

    public String generateAccessToken(String uid) {
        LOGGER.info("[generateAccessToken] Access Token Generated : {}", uid);
        return Jwts
                .builder()
                .subject(uid)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String generateRefreshToken(String uid) {
        LOGGER.info("[generateRefreshToken] Refresh Token Generated : {}", uid);
        return Jwts
                .builder()
                .subject(uid)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Claims validateToken(String token) {
        Jws<Claims> claimsJws = Jwts
                .parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token);
        return claimsJws.getPayload();
    }

    public String getUserNameFromToken(String token) {
        return validateToken(token).getSubject();
    }
}