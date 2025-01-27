package com.example.springboot.config.security;

import com.example.springboot.exception.CustomException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        // Token 분리하는 method 분리 가능함
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Bearer -> 공백 포함 7글자 제외하고 토큰 가져오기
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtUtil.validateToken(token);
                String username = claims.getSubject();
                if (username != null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);
                }
            } catch (Exception e) {
                LOGGER.error("Invalid token : {}", e.getMessage());
                throw new CustomException("Invalid token", HttpStatus.BAD_REQUEST);
            }
        }
        chain.doFilter(request, response);
    }
}