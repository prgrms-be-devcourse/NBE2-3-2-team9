package com.team9.anicare.domain.auth.controller;

import com.team9.anicare.domain.auth.dto.TokenResponseDTO;
import com.team9.anicare.domain.auth.security.CustomUserDetails;
import com.team9.anicare.domain.auth.security.JwtTokenProvider;
import com.team9.anicare.domain.auth.service.AuthService;
import com.team9.anicare.domain.auth.service.KakaoService;
import com.team9.anicare.domain.user.dto.LoginAdminDTO;
import com.team9.anicare.domain.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Tag(name = "auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    private  final JwtTokenProvider jwtTokenProvider;
    private final KakaoService kakaoService;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider, KakaoService kakaoService) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.kakaoService = kakaoService;
    }

    @Operation(summary = "관리자 로그인")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginAdminDTO loginAdminDTO, HttpServletResponse response) {
        Map<String, Object> responseMap = authService.login(loginAdminDTO, response);
        return ResponseEntity.ok(responseMap);
    }

    @Operation(summary = "관리자 로그아웃")
    @GetMapping("/logout")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String logout(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {
        return authService.logout(userDetails.getUserId(), response);
    }

    @PostMapping("/new-token")
    @Operation(summary = "토큰 재발급", description = "access토큰 재발급 API입니다.")
    public ResponseEntity<TokenResponseDTO> reCreateToken(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.reCreateToken(request));
    }

    @Operation(summary = "카카오 로그인")
    @GetMapping("/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=profile_nickname,account_email";

        response.sendRedirect(kakaoLoginUrl);
    }

    @Operation(summary = "카카오 로그인 콜백")
    @GetMapping("/kakao/callback")
    public ResponseEntity<Map<String, Object>> kakaoCallback(@RequestParam String code, HttpServletResponse response) {
        // 1. Kakao AccessToken 가져오기
        String kakaoAccessToken = kakaoService.getAccessToken(code);

        // 2. 사용자 검증 및 JWT AccessToken 생성
        UserResponseDTO userInfo = kakaoService.validateUser(kakaoAccessToken);

        // 3. RefreshToken 생성 및 쿠키에 저장
        String refreshToken = jwtTokenProvider.createRefreshToken(userInfo.getId());
        Cookie refreshTokenCookie = authService.createRefreshTokenCookie(refreshToken, "localhost");
        response.addCookie(refreshTokenCookie);

        // 4. AccessToken 반환
        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(userInfo.getAccessToken());
        // 5. userId와 함께 응답 반환
        Map<String, Object> responseMap = Map.of(
                "accessToken", tokenResponseDTO.getAccessToken(),
                "userId", userInfo.getId(),
                "role" , userInfo.getRole()
        );
        return ResponseEntity.ok(responseMap); // ResponseEntity로 반환
    }
}
