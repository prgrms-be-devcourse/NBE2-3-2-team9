package com.team9.anicare.domain.schedule.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.anicare.domain.auth.security.JwtTokenProvider;
import com.team9.anicare.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 환경 변수에서 값 가져오기
    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;
    @Value("${KAKAO_REDIRECT_URI_SCHEDULE}")
    private String kakaoRedirectUri;

    @Value("${KAKAO_TOKEN_URI}")
    private String kakaoTokenUri;

    @Value("${KAKAO_USER_INFO_URI}")
    private String kakaoUserInfoUri;
    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        String body = String.format(
                "grant_type=authorization_code&client_id=%s&redirect_uri=%s&code=%s",
                kakaoClientId,
                kakaoRedirectUri,
                code
        );

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoTokenUri,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.get("access_token").asText();
            } catch (Exception e) {
                throw new RuntimeException("Access Token 응답 파싱에 실패했습니다.\"", e);
            }
        } else {
            throw new RuntimeException("Access Token을 가져오지 못했습니다. 상태 코드: " + response.getStatusCode());
        }
    }
}
