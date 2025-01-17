package com.team9.anicare.domain.user.dto;

import com.team9.anicare.domain.user.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String nickname;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String profileImg;
    private Role role;

    // 생성자
    public UserResponseDTO(Long id, String nickname, String email, String accessToken, String refreshToken, String profileImg, Role role) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.profileImg = profileImg;
        this.role = role;
    }
}
