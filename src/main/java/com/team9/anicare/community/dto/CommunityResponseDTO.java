package com.team9.anicare.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunityResponseDTO {
    private String id;
    private String title;
    private String content;
    private String picture;
    private String animalSpecies;
    private int commentCount;
}
