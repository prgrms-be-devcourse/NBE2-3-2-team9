package com.team9.anicare.domain.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommunityResponseDTO {
    private Long id;
    private Long userId;
    private String name;
    private String profileImg;
    private String title;
    private String content;
    private String picture;
    private String animalSpecies;
    private int commentCount;
    private int likeCount;
    private boolean canEdit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
