package com.team9.anicare.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminResponseDTO {
    private String email;
    private String name;
    private String profileImg;
}
