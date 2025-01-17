package com.team9.anicare.domain.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.anicare.domain.auth.security.JwtTokenProvider;
import com.team9.anicare.domain.user.dto.UserResponseDTO;
import com.team9.anicare.domain.user.model.Role;
import com.team9.anicare.domain.user.model.User;
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
public class KakaoService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 환경 변수에서 값 가져오기
    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;

    @Value("${KAKAO_REDIRECT_URI}")
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

    public UserResponseDTO validateUser(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoUserInfoUri,
                HttpMethod.GET,
                request,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());

                JsonNode kakaoAccountNode = rootNode.path("kakao_account");
                JsonNode profileNode = kakaoAccountNode.path("profile");

                String nickname = profileNode.path("nickname").asText("Unknown");
                String email = kakaoAccountNode.path("email").asText("Unknown");
                String profileImg = profileNode.path("profile_image_url").asText("");

                // 사용자 저장 또는 기존 사용자 반환
                User user = userService.saveUser(nickname, email, profileImg, Role.USER);

                // JWT AccessToken 및 RefreshToken 생성
                String jwtAccessToken = jwtTokenProvider.createToken(user.getId());
                String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

                // RefreshToken 암호화 후 저장
                User updatedUser = user.toBuilder()
                        .refreshtoken(passwordEncoder.encode(refreshToken)) // RefreshToken만 변경
                        .build();
                userService.updateUser(updatedUser);

                // UserResponseDTO 반환
                return new UserResponseDTO(user.getId(), nickname, email, jwtAccessToken, refreshToken, profileImg, user.getRole());
            } catch (Exception e) {
                throw new RuntimeException("사용자 정보 응답 파싱에 실패했습니다.", e);
            }
        } else {
            throw new RuntimeException("사용자 정보를 가져오지 못했습니다. 상태 코드" + response.getStatusCode());
        }
    }
}
