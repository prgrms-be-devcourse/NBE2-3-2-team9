package com.team9.anicare.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CommunityDetailResponseDTO {
    private CommunityResponseDTO community;
    private List<CommentResponseDTO> comment;
}
