package com.team9.anicare.user.controller;

import com.team9.anicare.auth.security.CustomUserDetails;
import com.team9.anicare.user.dto.CreateAdminDTO;
import com.team9.anicare.user.dto.UpdateAdminDTO;
import com.team9.anicare.user.dto.UserDetailResponseDTO;
import com.team9.anicare.user.dto.UserResponseDTO;
import com.team9.anicare.user.model.User;
import com.team9.anicare.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;


    // 관리자 생성
    @PostMapping("/admin")
    public ResponseEntity<CreateAdminDTO> signup(@Valid @RequestBody CreateAdminDTO createAdminDTO) {
        // 서비스에서 관리자 생성
        CreateAdminDTO savedAdmin = userService.createAdmin(createAdminDTO);

        // 201 Created와 함께 반환
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedAdmin);
    }

    // 관리자 프로필 조회
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> adminProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
     User user = userService.adminInfo(userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(user);
    }

    // 관리자 정보 업데이트
    @PutMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminUpdate(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @Valid @RequestBody UpdateAdminDTO updateAdminDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.adminUpdate(userDetails.getUserId(), updateAdminDTO ));
    }

    // 사용자 삭제
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> delete(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.deleteUser(userDetails.getUserId()));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDetailResponseDTO> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUserInfo(userDetails.getUserId()));
    }

}
