package com.team9.anicare.user.dto;

import com.team9.anicare.community.model.Community;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String nickname;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String profileImg;

    // 생성자
    public UserResponseDTO(Long id, String nickname, String email, String accessToken, String refreshToken, String profileImg) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.profileImg = profileImg;
    }
}
