package com.team9.anicare.auth.service;

import com.team9.anicare.auth.dto.TokenResponseDTO;
import com.team9.anicare.auth.security.JwtTokenProvider;
import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.response.Result;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.user.dto.LoginAdminDTO;
import com.team9.anicare.user.model.User;
import com.team9.anicare.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private  final JwtTokenProvider jwtTokenProvider;

    private final String cookieName = "REFRESHTOKEN";


    public TokenResponseDTO login(LoginAdminDTO loginAdminDTO , HttpServletResponse response) {
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
        // Cookie에 refreshToken 값 추가
        Cookie cookie = new Cookie(cookieName, refreshToken);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(604800);
        response.addCookie(cookie);
        user.setRefreshtoken(passwordEncoder.encode(refreshToken));
        userRepository.save(user);

        return new TokenResponseDTO(accessToken);
    }

    public String logout(Long userId, HttpServletResponse response) {
        // 1. 사용자 조회
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.get();

        // 2. RefreshToken 초기화
        user.setRefreshtoken(null);
        userRepository.save(user);

        // 3. Cookie에서 RefreshToken 삭제
        Cookie cookie = new Cookie(cookieName, null); // 쿠키 값 제거
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

}
