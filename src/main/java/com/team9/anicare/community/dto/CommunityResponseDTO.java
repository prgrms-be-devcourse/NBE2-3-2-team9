package com.team9.anicare.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunityResponseDTO {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String picture;
    private String animalSpecies;
    private int commentCount;
    private int likeCount;
    private boolean canEdit;
}
