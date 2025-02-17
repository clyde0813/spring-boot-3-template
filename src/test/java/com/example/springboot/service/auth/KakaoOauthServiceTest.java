package com.example.springboot.service.auth;

import com.example.springboot.client.auth.KakaoOauthClient;
import com.example.springboot.config.security.JwtUtil;
import com.example.springboot.data.dto.auth.kakao.KakaoUserInfoResponseDto;
import com.example.springboot.data.entity.auth.User;
import com.example.springboot.data.entity.auth.UserOauth;
import com.example.springboot.exception.CustomException;
import com.example.springboot.repository.UserOauthRepository;
import com.example.springboot.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("KakaoOauthService 단위 테스트")
class KakaoOauthServiceTest {
    @Mock
    private KakaoOauthClient kakaoOauthClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserOauthRepository userOauthRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private KakaoOauthService kakaoOauthService;

    @Test
    @DisplayName("KakaoOauthService - Kakao Access Token 발급")
    void getAccessToken() {
        String code = "authCode";
        String accessToken = "accessToken";
        when(kakaoOauthClient.getAccessToken(code)).thenReturn(accessToken);

        String result = kakaoOauthService.getAccessToken(code);

        assertEquals(accessToken, result);
    }

    @Test
    @DisplayName("KakaoOauthService - Kakao Access Token 발급 fail (kakao 4xx)")
    void getAccessToken_fail4xx() {
        String code = "authCode";
        when(kakaoOauthClient.getAccessToken(code)).thenThrow(new CustomException("Invalid Parameter", HttpStatus.BAD_REQUEST));

        CustomException exception = assertThrows(CustomException.class, () -> kakaoOauthService.getAccessToken(code));

        assertEquals("Invalid Parameter", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    @DisplayName("KakaoOauthService - Kakao Access Token 발급 fail (kakao 5xx)")
    void getAccessToken_fail5xx() {
        String code = "authCode";
        when(kakaoOauthClient.getAccessToken(code)).thenThrow(new CustomException("Kakao Server Error", HttpStatus.INTERNAL_SERVER_ERROR));

        CustomException exception = assertThrows(CustomException.class, () -> kakaoOauthService.getAccessToken(code));

        assertEquals("Kakao Server Error", exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    @DisplayName("KakaoOauthService - get User Info and Return Token (신규 가입)")
    void getUserInfoAndReturnToken_newUser() {
        String accessToken = "accessToken";
        KakaoUserInfoResponseDto kakaoUserInfo = getKakaoUserInfoResponseDto(12345L);
        String id4kakao = "kakao" + kakaoUserInfo.getId();
        when(kakaoOauthClient.getUserInfo(accessToken)).thenReturn(kakaoUserInfo);

        User newUser = new User();
        newUser.setId("userId");
        newUser.setNickname(kakaoUserInfo.kakaoAccount.profile.nickName);
        newUser.setProfileImageUrl(kakaoUserInfo.kakaoAccount.profile.profileImageUrl);
        newUser.setCreatedAt(LocalDateTime.now());
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserOauth newUserOauth = new UserOauth();
        newUserOauth.setId(id4kakao);
        newUserOauth.setUserId("userId");
        newUserOauth.setProvider("kakao");
        when(userOauthRepository.save(any(UserOauth.class))).thenReturn(newUserOauth);


        when(jwtUtil.generateAccessToken("userId")).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken("userId")).thenReturn("refreshToken");

        Map<String, String> result = kakaoOauthService.getUserInfoAndReturnToken(accessToken);

        assertEquals("accessToken", result.get("access_token"));
        assertEquals("refreshToken", result.get("refresh_token"));
    }

    @Test
    @DisplayName("KakaoOauthService - get User Info and Return Token (기존 가입)")
    void getUserInfoAndReturnToken_existingUser() {
        String accessToken = "accessToken";
        KakaoUserInfoResponseDto kakaoUserInfo = getKakaoUserInfoResponseDto(12345L);
        String id4kakao = "kakao" + kakaoUserInfo.getId();
        when(kakaoOauthClient.getUserInfo(accessToken)).thenReturn(kakaoUserInfo);

//        User user = new User();
//        user.setId("userId");
//        user.setNickname(kakaoUserInfo.kakaoAccount.profile.nickName);
//        user.setProfileImageUrl(kakaoUserInfo.kakaoAccount.profile.profileImageUrl);
//        user.setCreatedAt(LocalDateTime.now());
//        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserOauth userOauth = new UserOauth();
        userOauth.setId(id4kakao);
        userOauth.setUserId("userId");
        userOauth.setProvider("kakao");
        when(userOauthRepository.findById(id4kakao)).thenReturn(Optional.of(userOauth));

        when(jwtUtil.generateAccessToken("userId")).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken("userId")).thenReturn("refreshToken");

        Map<String, String> result = kakaoOauthService.getUserInfoAndReturnToken(accessToken);

        assertEquals("accessToken", result.get("access_token"));
        assertEquals("refreshToken", result.get("refresh_token"));
    }

    private static KakaoUserInfoResponseDto getKakaoUserInfoResponseDto(Long kakaoId) {
        KakaoUserInfoResponseDto kakaoUserInfo = new KakaoUserInfoResponseDto();
        KakaoUserInfoResponseDto.KakaoAccount kakaoAccount = new KakaoUserInfoResponseDto.KakaoAccount();
        KakaoUserInfoResponseDto.KakaoAccount.Profile profile = new KakaoUserInfoResponseDto.KakaoAccount.Profile();

        kakaoUserInfo.setId(kakaoId);

        profile.setNickName("nickname");
        profile.setProfileImageUrl("profileImageUrl");

        kakaoAccount.setProfile(profile);
        kakaoUserInfo.setKakaoAccount(kakaoAccount);
        return kakaoUserInfo;
    }


}