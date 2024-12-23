package com.team9.anicare.auth.controller;

import com.team9.anicare.auth.dto.TokenResponseDTO;
import com.team9.anicare.auth.security.CustomUserDetails;
import com.team9.anicare.auth.security.JwtTokenProvider;
import com.team9.anicare.auth.service.AuthService;
import com.team9.anicare.auth.service.KakaoService;
import com.team9.anicare.common.response.Result;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.user.dto.LoginAdminDTO;
import com.team9.anicare.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginAdminDTO loginAdminDTO, HttpServletResponse response) {
        TokenResponseDTO tokenResponseDTO = authService.login(loginAdminDTO, response);
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponseDTO);
    }

    @GetMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> logout(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.logout(userDetails.getUserId(), response));
    }

    @PostMapping("/new-token")
    @Operation(summary = "토큰 재발급", description = "access토큰 재발급 API입니다.")
    public ResponseEntity<TokenResponseDTO> reCreateToken(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.reCreateToken(request));
    }
    @GetMapping("/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=profile_nickname,account_email";

        response.sendRedirect(kakaoLoginUrl);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<TokenResponseDTO> kakaoCallback(@RequestParam String code, HttpServletResponse response) {
        // 1. Kakao AccessToken 가져오기
        String kakaoAccessToken = kakaoService.getAccessToken(code);

        // 2. 사용자 검증 및 JWT AccessToken 생성
        UserResponseDTO userInfo = kakaoService.validateUser(kakaoAccessToken);

        // 3. RefreshToken 생성 및 쿠키에 저장
        String refreshToken = jwtTokenProvider.createRefreshToken(userInfo.getId());
        Cookie cookie = new Cookie("REFRESHTOKEN", refreshToken);
        cookie.setHttpOnly(true); // 보안 강화
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setMaxAge(604800); // 7일
        response.addCookie(cookie);

        // 4. AccessToken 반환
        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(userInfo.getAccessToken());

        return ResponseEntity.ok(tokenResponseDTO); // ResponseEntity로 반환
    }


}
