package com.example.springboot.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthentication implements Authentication {
    private final String userId;
    private boolean authenticated = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한 정보가 없는 경우
    }

    @Override
    public Object getCredentials() {
        return null; // JWT 토큰 자체는 credentials로 사용하지 않음
    }

    @Override
    public Object getDetails() {
        return null; // 추가적인 사용자 정보가 없는 경우
    }

    @Override
    public String getPrincipal() {
        return userId; // 사용자 식별 정보 (예: 이메일 또는 아이디)
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return userId;
    }
}
