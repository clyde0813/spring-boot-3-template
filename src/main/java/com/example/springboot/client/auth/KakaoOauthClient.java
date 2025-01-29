package com.example.springboot.client.auth;

import com.example.springboot.constant.KakaoOauthConstants;
import com.example.springboot.data.dto.auth.kakao.KakaoTokenResponseDto;
import com.example.springboot.data.dto.auth.kakao.KakaoUserInfoResponseDto;
import com.example.springboot.exception.CustomException;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class KakaoOauthClient {
    private final WebClient webClient;
    private final Logger LOGGER = LoggerFactory.getLogger(KakaoOauthClient.class);

    @Value("${kakao.client_id}")
    private String kakaoClientId;

    public String getAccessToken(String code) {
        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient
                .create(KakaoOauthConstants.KAKAO_TOKEN_URL_HOST)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", kakaoClientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new CustomException("Invalid Parameter", HttpStatus.BAD_REQUEST)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new CustomException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();


        LOGGER.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
        LOGGER.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());
        //제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
        LOGGER.info(" [Kakao Service] Id Token ------> {}", kakaoTokenResponseDto.getIdToken());
        LOGGER.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDto.getScope());

        return kakaoTokenResponseDto.getAccessToken();
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        KakaoUserInfoResponseDto userInfo = WebClient
                .create(KakaoOauthConstants.KAKAO_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new CustomException("Invalid Parameter", HttpStatus.BAD_REQUEST)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new CustomException("Kakao Server Error", HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        LOGGER.info("[ Kakao Service ] Auth ID ---> {} ", userInfo.getId());
        LOGGER.info("[ Kakao Service ] NickName ---> {} ", userInfo
                .getKakaoAccount()
                .getProfile()
                .getNickName());
        LOGGER.info("[ Kakao Service ] ProfileImageUrl ---> {} ", userInfo
                .getKakaoAccount()
                .getProfile()
                .getProfileImageUrl());

        return userInfo;
    }
}
