package com.team9.anicare.domain.auth.service;

import com.team9.anicare.domain.auth.dto.TokenResponseDTO;
import com.team9.anicare.domain.auth.security.JwtTokenProvider;
import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.domain.user.dto.LoginAdminDTO;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private  final JwtTokenProvider jwtTokenProvider;

    private final String cookieName = "REFRESHTOKEN";




    public Map<String, Object> login(LoginAdminDTO loginAdminDTO , HttpServletResponse response) {
        Optional<User> optionalUser = userRepository.findByEmail(loginAdminDTO.getEmail());
        if (optionalUser.isEmpty()) {
            throw  new CustomException(ResultCode.NOT_EXISTS_USER);
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(loginAdminDTO.getPassword(), user.getPassword())) {
           throw new CustomException(ResultCode.INVALID_USER_PASSWORD);
        }
        String accessToken = jwtTokenProvider.createToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        Cookie refreshTokenCookie = createRefreshTokenCookie(refreshToken, "localhost");
        response.addCookie(refreshTokenCookie);
        User updatedUser = User.builder()
                .id(user.getId())
                .name(user.getName())
                .profileImg(user.getProfileImg())
                .email(user.getEmail()) // 누락 방지
                .password(user.getPassword())
                .role(user.getRole()) // 다른 필드도 명시
                .pets(user.getPets())
                .socialAccessToken(user.getSocialAccessToken())
                .years_of_experience(user.getYears_of_experience())
                .refreshtoken(passwordEncoder.encode(refreshToken)) // 변경 필드
                .chatMessages(user.getChatMessages())
                .chatRooms(user.getChatRooms())
                .communities(user.getCommunities())
                .comments(user.getComments())
                .build();

        userRepository.save(updatedUser);

        Map<String, Object> responseMap = Map.of(
            "accessToken", accessToken,
            "userId", user.getId(),
            "role", user.getRole()
        );

        return responseMap;
    }

    public String logout(Long userId, HttpServletResponse response) {
        // 1. 사용자 조회
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.get();

        // 2. RefreshToken 초기화
        User updatedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail()) // 누락 방지
                .password(user.getPassword())
                .profileImg(user.getProfileImg())
                .socialAccessToken(user.getSocialAccessToken())
                .name(user.getName())
                .role(user.getRole()) // 다른 필드도 명시
                .pets(user.getPets())
                .comments(user.getComments())
                .communities(user.getCommunities())
                .chatRooms(user.getChatRooms())
                .chatMessages(user.getChatMessages())
                .years_of_experience(user.getYears_of_experience())
                .refreshtoken(null) // 변경 필드
                .build();

        userRepository.save(updatedUser);

        // 3. Cookie에서 RefreshToken 삭제
        Cookie cookie = new Cookie("REFRESHTOKEN", null); // 쿠키 값 제거
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 만료 시간 0으로 설정
        response.addCookie(cookie);

        // 4. 성공 결과 반환
        return "로그아웃 성공";
    }

    public TokenResponseDTO reCreateToken(HttpServletRequest request) {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                throw new CustomException(ResultCode.TOKEN_EXPIRED);
            }

            long userId = 0;
            String refreshToken = null;

            // 쿠키에서 refreshToken과 userId 추출
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    refreshToken = cookie.getValue();
                    userId = jwtTokenProvider.getId(refreshToken);
                }
            }

            if (refreshToken == null || userId == 0) {
                throw new CustomException(ResultCode.TOKEN_EXPIRED);
            }

            // DB에서 사용자 조회 및 refreshToken 검증
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                throw new CustomException(ResultCode.NOT_EXISTS_USER);
            }

            User user = optionalUser.get();
            if (!passwordEncoder.matches(refreshToken, user.getRefreshtoken())) {
                throw new CustomException(ResultCode.INVALID_TOKEN);
            }

            // accessToken 재발급
            String newAccessToken = jwtTokenProvider.createToken(userId);

            return new TokenResponseDTO(newAccessToken);

    }

    public Cookie createRefreshTokenCookie(String refreshToken, String domain) {
        Cookie cookie = new Cookie("REFRESHTOKEN", refreshToken);
        cookie.setHttpOnly(true); // 보안 강화
        cookie.setPath("/");
        cookie.setDomain(domain); // 도메인 설정
        cookie.setMaxAge(604800); // 7일 (초 단위)
        return cookie;
    }

}
