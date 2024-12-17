package com.team9.anicare.community.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponseDTO {
    private Long Id;
    private Long communityId;
    private String content;
}
