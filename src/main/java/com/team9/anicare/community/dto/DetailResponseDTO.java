package com.team9.anicare.community.dto;

import com.team9.anicare.common.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DetailResponseDTO {
    private CommunityResponseDTO community;
    private PageDTO<CommentResponseDTO> comment;
}
