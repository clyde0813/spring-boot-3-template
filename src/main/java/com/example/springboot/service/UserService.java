package com.example.springboot.service;

import com.example.springboot.data.entity.auth.User;
import com.example.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByUserId(String userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
