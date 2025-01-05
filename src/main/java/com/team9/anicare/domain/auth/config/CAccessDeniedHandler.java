package com.team9.anicare.domain.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.anicare.common.response.Result;
import com.team9.anicare.common.exception.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 기본 응답 값
        ResultCode resultCode = ResultCode.FORBIDDEN;

        // SecurityContext에서 권한(Role) 확인
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getAuthorities() != null) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                resultCode = ResultCode.ACCESS_DENIED_ADMIN; // 관리자 권한 부족
            } else {
                resultCode = ResultCode.ACCESS_DENIED_USER; // 사용자 권한 부족
            }
        }
        // 응답 생성
        Result<Void> errorResponse = new Result<>(resultCode);

        // JSON 변환 및 응답
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(resultCode.getCode()); // 상태 코드 설정
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
