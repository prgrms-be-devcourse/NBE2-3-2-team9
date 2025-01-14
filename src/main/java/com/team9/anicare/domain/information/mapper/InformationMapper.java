package com.team9.anicare.domain.information.mapper;


import com.team9.anicare.domain.information.dto.InformationResponseDTO;
import com.team9.anicare.domain.information.model.Information;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InformationMapper {

    @Mapping(source = "breed.species.name", target = "speciesName")
    @Mapping(source = "breed.name", target = "breedName")
    InformationResponseDTO toDto(Information information);
}

