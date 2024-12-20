package com.team9.anicare.auth.service;

import com.team9.anicare.auth.security.JwtTokenProvider;
import com.team9.anicare.common.Result;
import com.team9.anicare.common.ResultCode;
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


    public Result login(LoginAdminDTO loginAdminDTO , HttpServletResponse response) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(loginAdminDTO.getEmail());
            if (optionalUser.isEmpty()) {
                return new Result(ResultCode.NOT_EXISTS_USER);
            }
            User user = optionalUser.get();
            if (!passwordEncoder.matches(loginAdminDTO.getPassword(), user.getPassword())) {
                return new Result(ResultCode.INVALID_USER_PASSWORD);
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

            return new Result(ResultCode.SUCCESS, accessToken);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.DB_ERROR);
        }
    }

    public Result logout(Long userId, HttpServletResponse response) {
        try {
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
            return new Result(ResultCode.SUCCESS, "로그아웃 성공");
        } catch (Exception e) {
            // 기타 예외 처리
            System.out.println(e.getMessage());
            return new Result(ResultCode.DB_ERROR);
        }
    }

    public Result reCreateToken(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return new Result(ResultCode.TOKEN_EXPIRED, "쿠키에서 토큰을 찾을 수 없습니다.");
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
                return new Result(ResultCode.TOKEN_EXPIRED);
            }

            // DB에서 사용자 조회 및 refreshToken 검증
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                return new Result(ResultCode.NOT_EXISTS_USER);
            }

            User user = optionalUser.get();
            if (!passwordEncoder.matches(refreshToken, user.getRefreshtoken())) {
                return new Result(ResultCode.INVALID_TOKEN);
            }

            // accessToken 재발급
            String newAccessToken = jwtTokenProvider.createToken(userId);

            return new Result(ResultCode.SUCCESS, newAccessToken);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return new Result(ResultCode.DB_ERROR, "토큰 재발급 중 오류가 발생했습니다.");
        }
    }

}
