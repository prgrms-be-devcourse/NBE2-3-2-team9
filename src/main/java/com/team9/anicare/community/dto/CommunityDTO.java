package com.team9.anicare.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CommunityDTO {
    private Long id;
    private String title;
    private String content;
    private String animalSpecies;
    private String picture;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
