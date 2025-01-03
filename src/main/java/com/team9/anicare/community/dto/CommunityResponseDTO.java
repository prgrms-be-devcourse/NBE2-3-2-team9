package com.team9.anicare.community.dto;

import com.team9.anicare.community.model.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
