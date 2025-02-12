package com.example.springboot.data.entity.auth;

import com.example.springboot.data.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "nickname", unique = false, nullable = true)
    private String nickname;

    @Column(name = "profile_image_url", nullable = true)
    private String profileImageUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

//    @Column(nullable = false)
//    private String password;

    public UserDto getUserDto() {
        UserDto userDto = new UserDto();
        userDto.setNickname(nickname);
        userDto.setProfileImageUrl(profileImageUrl);
        return userDto;
    }
}
