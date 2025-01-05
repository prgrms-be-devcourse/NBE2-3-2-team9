package com.team9.anicare.domain.community.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDTO {
    private Long Id;
    private Long communityId;
    private Long userId;
    private String name;
    private String profileImg;
    private String content;
    private boolean canEdit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
