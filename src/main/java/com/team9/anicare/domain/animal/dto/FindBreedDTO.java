package com.team9.anicare.domain.animal.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
public class FindBreedDTO {
    private Long id;
    private Long speciesId;
    private String name;
}
