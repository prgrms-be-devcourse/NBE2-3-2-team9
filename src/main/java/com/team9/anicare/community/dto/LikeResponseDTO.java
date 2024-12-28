package com.team9.anicare.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeResponseDTO {
    private Long id;
    private Long communityId;
    private Long userId;
}
