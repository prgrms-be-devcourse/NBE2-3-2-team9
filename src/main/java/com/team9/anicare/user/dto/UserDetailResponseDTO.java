package com.team9.anicare.user.dto;

import com.team9.anicare.common.entities.CommonEntity;
import com.team9.anicare.community.dto.CommunityDTO;
import com.team9.anicare.community.dto.CommunityRequestDTO;
import com.team9.anicare.community.dto.CommunityResponseDTO;
import com.team9.anicare.community.model.Community;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserDetailResponseDTO  {
    private Long id;
    private String name;
    private String email;
    private String profileImg;
    private List<CommunityDTO> communities;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
