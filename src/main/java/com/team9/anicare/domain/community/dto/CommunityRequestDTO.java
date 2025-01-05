package com.team9.anicare.domain.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunityRequestDTO {
    private String title;
    private String content;
    private String animalSpecies;
}
