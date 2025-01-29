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

    //static 으로 만들고자 하였으나, @Value 어노테이션은 static 변수에 적용이 불가능함
    @Autowired
    public JwtUtil(
            @Value("${jwt.secret.key}") String secretKey,
            @Value("${jwt.access.expiration}") long accessExpiration,
            @Value("${jwt.refresh.expiration}") long refreshExpiration
    ) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.ACCESS_TOKEN_EXPIRATION_TIME = accessExpiration;
        this.REFRESH_TOKEN_EXPIRATION_TIME = refreshExpiration;
    }

    // 접근 토큰 생성
    public String generateAccessToken(String userId) {
        LOGGER.info("[generateAccessToken] Access Token Generated : {}", userId);
        return Jwts
                .builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 갱신 토큰 생성
    public String generateRefreshToken(String userId) {
        LOGGER.info("[generateRefreshToken] Refresh Token Generated : {}", userId);
        return Jwts
                .builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 토큰 유효성 검사
    public Claims validateToken(String token) {
        LOGGER.info("[validateToken] Token validation check start");
        Jws<Claims> claimsJws = Jwts
                .parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token);
        LOGGER.info("[validateToken] Token validation check end : {}", claimsJws.getPayload());
        return claimsJws.getPayload();
    }

    public String getUserNameFromToken(String token) {
        return validateToken(token).getSubject();
    }
}