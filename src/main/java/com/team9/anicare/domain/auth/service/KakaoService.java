package com.team9.anicare.domain.auth.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.team9.anicare.domain.auth.client.KakaoClient;
import com.team9.anicare.domain.auth.security.JwtTokenProvider;
import com.team9.anicare.domain.user.dto.UserResponseDTO;
import com.team9.anicare.domain.user.model.Role;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final KakaoClient kakaoClient;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;

    @Value("${KAKAO_REDIRECT_URI}")
    private String kakaoRedirectUri;

    public String getAccessToken(String code) {
        return kakaoClient.getAccessToken(kakaoClientId, kakaoRedirectUri, code);
    }

    public UserResponseDTO validateUser(String kakaoAccessToken) {
        JsonNode userInfo = kakaoClient.getUserInfo(kakaoAccessToken);

        JsonNode kakaoAccountNode = userInfo.path("kakao_account");
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
                .refreshtoken(passwordEncoder.encode(refreshToken))
                .socialAccessToken(kakaoAccessToken)
                .build();
        userService.updateUser(updatedUser);

        return new UserResponseDTO(
                user.getId(),
                nickname,
                email,
                jwtAccessToken,
                refreshToken,
                profileImg,
                user.getRole()
        );
    }
}
