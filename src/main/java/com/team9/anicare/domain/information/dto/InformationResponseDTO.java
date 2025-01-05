package com.team9.anicare.domain.information.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InformationResponseDTO {
    private Long id;
    private String speciesName;
    private String breedName;
    private String picture;
    private String age;
    private String weight;
    private String height;
    private String guide;
    private String description;
    private int hit;
}