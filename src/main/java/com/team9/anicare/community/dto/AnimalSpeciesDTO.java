package com.team9.anicare.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AnimalSpeciesDTO {
    private List<String> animalSpeciesList;
}
