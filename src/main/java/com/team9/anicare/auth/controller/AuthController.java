package com.team9.anicare.auth.controller;

import com.team9.anicare.auth.security.CustomUserDetails;
import com.team9.anicare.auth.service.AuthService;
import com.team9.anicare.common.Result;
import com.team9.anicare.user.dto.LoginAdminDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginAdminDTO loginAdminDTO, HttpServletResponse response) {
        return authService.login(loginAdminDTO, response);
    }

    @GetMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public Result adminProfile(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {
        Long id = userDetails.getUserId(); // User ID 가져오기
        return authService.logout(id, response);
    }

    @PostMapping("/new-token")
    @Operation(summary = "토큰 재발급", description = "access토큰 재발급 API입니다.")
    public Result reCreateToken(HttpServletRequest request) {
        return authService.reCreateToken(request);
    }




}
