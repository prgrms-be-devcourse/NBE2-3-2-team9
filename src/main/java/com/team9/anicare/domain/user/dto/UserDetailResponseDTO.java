package com.team9.anicare.domain.user.dto;

import com.team9.anicare.domain.community.dto.CommunityDTO;
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
    private int years_of_experience;
    private List<CommunityDTO> communities;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
