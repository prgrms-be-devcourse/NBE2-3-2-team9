package com.team9.anicare.community.dto;

import com.team9.anicare.community.model.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentResponseDTO {
    private Long Id;
    private Long communityId;
    private Long userId;
    private String content;
    private boolean canEdit;
}
