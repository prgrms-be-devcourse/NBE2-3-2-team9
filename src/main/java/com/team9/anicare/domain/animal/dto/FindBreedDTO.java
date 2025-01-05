package com.team9.anicare.domain.animal.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FindBreedDTO {
    private Long id;
    private Long speciesId;
    private String name;
}
