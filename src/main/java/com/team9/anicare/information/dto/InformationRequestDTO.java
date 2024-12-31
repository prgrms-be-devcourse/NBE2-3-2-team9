package com.team9.anicare.information.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InformationRequestDTO {
    private String speciesName;
    private String breedName;
    private String age;
    private String weight;
    private String height;
    private String guide;
    private String description;
}
