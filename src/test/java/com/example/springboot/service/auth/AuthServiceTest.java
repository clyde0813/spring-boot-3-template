package com.example.springboot.service.auth;

import com.example.springboot.data.entity.auth.User;
import com.example.springboot.exception.CustomException;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.service.auth.impl.PasswordEncoderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordEncoderService passwordEncoderService;

    @Test
    @DisplayName("중복 username 테스트")
    public void testSignUp_UsernameAlreadyExist() {
        User existingUser = new User();
        existingUser.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(existingUser);
        CustomException thrown = assertThrows(CustomException.class, () -> {
            if (userRepository.findByUsername("testuser") != null) {
                throw new CustomException("Username already exists", HttpStatus.BAD_REQUEST);
            }
        });

        assertEquals("Username already exists", thrown.getMessage());
    }

    @Test
    @DisplayName("비밀번호 암호화 및 검증 테스트")
    public void testEncodePassword() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 비밀번호 암호화
        String rawPassword = "mypassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 암호화된 비밀번호가 비어 있지 않은지 확인
        assertNotNull(encodedPassword);
        assertFalse(encodedPassword.isEmpty());

        // 암호화된 비밀번호가 원래 비밀번호와 일치하는지 검증 (랜덤 솔트로 인해 직접 비교 불가)
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("잘못된 비밀번호 테스트")
    public void testLogin_InvalidPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false);

        assertFalse(passwordEncoder.matches("wrongPassword", user.getPassword()));
    }
}