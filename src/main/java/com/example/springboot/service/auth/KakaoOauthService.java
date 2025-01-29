package com.example.springboot.service.auth;

import com.example.springboot.client.auth.KakaoOauthClient;
import com.example.springboot.config.security.JwtUtil;
import com.example.springboot.data.dto.auth.kakao.KakaoUserInfoResponseDto;
import com.example.springboot.data.entity.auth.User;
import com.example.springboot.data.entity.auth.UserOauth;
import com.example.springboot.exception.CustomException;
import com.example.springboot.repository.UserOauthRepository;
import com.example.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoOauthService {
    private final KakaoOauthClient kakaoOauthClient;
    private final UserRepository userRepository;
    private final UserOauthRepository userOauthRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final JwtUtil jwtUtil;

    private final Logger LOGGER = LoggerFactory.getLogger(KakaoOauthService.class);

    public String getAccessToken(String code) {
        return kakaoOauthClient.getAccessToken(code);
    }

    public Map<String, String> getUserInfoAndReturnToken(String accessToken) {
        LOGGER.info("[getUserInfoAndReturnToken] : {}", accessToken);
        KakaoUserInfoResponseDto userInfo = kakaoOauthClient.getUserInfo(accessToken);
        LOGGER.info("[getUserInfoAndReturnToken] - nickname : {}", userInfo.kakaoAccount.profile.nickName);
        String id4kakao = "kakao" + userInfo.id;
        Optional<UserOauth> userOauth = userOauthRepository.findById(id4kakao);
        LOGGER.info("[getUserInfoAndReturnToken] - userPresent : {}", userOauth.isPresent());
        if (userOauth.isEmpty()) {
            User newUser = new User();
            newUser.setUsername(id4kakao);
            newUser.setNickname(userInfo.kakaoAccount.profile.nickName);
            newUser.setProfileImageUrl(userInfo.kakaoAccount.profile.profileImageUrl);
            newUser.setPassword(passwordEncoderService.encode(id4kakao + System.currentTimeMillis()));
            userRepository.save(newUser);
            UserOauth newUserOauth = new UserOauth();
            newUserOauth.setId(id4kakao);
            newUserOauth.setUserId(newUser.getId());
            newUserOauth.setProvider("kakao");
            userOauthRepository.save(newUserOauth);
        }
        if (userOauthRepository.findById(id4kakao).isPresent() && userRepository.findById(userOauthRepository.findById(id4kakao).get().getUserId()).isPresent()) {
            String userId = userOauthRepository
                    .findById(id4kakao)
                    .get()
                    .getUserId();
            return Map.of("access_token", jwtUtil.generateAccessToken(userId), "refresh_token", jwtUtil.generateRefreshToken(userId));
        } else {
            throw new CustomException("User not found with Oauth ID : " + id4kakao, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
