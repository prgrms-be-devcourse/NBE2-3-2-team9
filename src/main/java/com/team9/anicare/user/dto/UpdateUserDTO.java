package com.team9.anicare.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserDTO {


    @Schema(description = "유저이름", example = "유저이름")
    private String name; // 선택 사항으로 처리

    @Schema(description = "반려동물 경험")
    private int years_of_experience ;
}
