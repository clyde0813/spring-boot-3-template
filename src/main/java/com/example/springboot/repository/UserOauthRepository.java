package com.example.springboot.repository;

import com.example.springboot.data.entity.auth.UserOauth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOauthRepository extends JpaRepository<UserOauth, String> {
    Optional<UserOauth> findById(String id);
}
