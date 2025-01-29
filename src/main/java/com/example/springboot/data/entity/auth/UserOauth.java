package com.example.springboot.data.entity.auth;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_oauth")
public class UserOauth {
    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "provider")
    private String provider;
}
