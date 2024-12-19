package com.team9.anicare.user.controller;

import com.team9.anicare.auth.security.CustomUserDetails;
import com.team9.anicare.common.Result;
import com.team9.anicare.user.dto.CreateAdminDTO;
import com.team9.anicare.user.dto.UpdateAdminDTO;
import com.team9.anicare.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;


    // 관리자 생성
    @PostMapping
    public Result signup(@Valid @RequestBody CreateAdminDTO createAdminDTO) {
        return userService.createAdmin(createAdminDTO);
    }

    // 관리자 프로필 조회
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result adminProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.adminInfo(userDetails.getUserId());
    }

    // 관리자 정보 업데이트
    @PutMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result adminUpdate(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @Valid @RequestBody UpdateAdminDTO updateAdminDTO) {
        return userService.adminUpdate(userDetails.getUserId(), updateAdminDTO);
    }

    // 사용자 삭제
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public Result delete(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.deleteUser(userDetails.getUserId());
    }

}
