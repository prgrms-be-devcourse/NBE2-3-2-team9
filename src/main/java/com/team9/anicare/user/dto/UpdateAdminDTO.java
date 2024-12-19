package com.team9.anicare.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAdminDTO {

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 최소 하나의 문자, 숫자, 특수 문자를 포함해야 합니다."
    )
    @Schema(description = "비밀번호", example = "newpassword123!")
    private String password; // 선택 사항으로 처리

    @Schema(description = "관리자 이름", example = "새로운 관리자 이름")
    private String name; // 선택 사항으로 처리

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/new-profile.jpg")
    private String profileImg; // 선택 사항으로 처리
}
