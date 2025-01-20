package com.team9.anicare.domain.user.controller;

import com.team9.anicare.domain.auth.security.CustomUserDetails;
import com.team9.anicare.domain.user.dto.*;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "user", description = "회원/관리자 API")
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;


    // 관리자 생성
    @Operation(summary = "관리자 회원가입")
    @PostMapping("/admin")
    public ResponseEntity<AdminResponseDTO> signup(@Valid @RequestBody CreateAdminDTO createAdminDTO) {
        // 서비스에서 관리자 생성
        AdminResponseDTO savedAdmin = userService.createAdmin(createAdminDTO);

        // 201 Created와 함께 반환
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedAdmin);
    }

    // 관리자 프로필 조회
    @Operation(summary = "관리자 정보 조회")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> adminProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
     User user = userService.adminInfo(userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(user);
    }

    // 관리자 정보 업데이트
    @Operation(summary = "관리자 정보 수정")
    @PutMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminUpdate(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @Valid @RequestBody UpdateAdminDTO updateAdminDTO) {
        return userService.adminUpdate(userDetails.getUserId(), updateAdminDTO );
    }

    @Operation(summary = "일반 회원 정보 수정")
    @PutMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userUpdate(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        return userService.userUpdate(userDetails.getUserId(), updateUserDTO );
    }


    // 사용자 삭제
    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.deleteUser(userDetails.getUserId());
    }

    @Operation(summary = "일반 회원 정보 조회")
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDetailResponseDTO> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUserInfo(userDetails.getUserId()));
    }

}
