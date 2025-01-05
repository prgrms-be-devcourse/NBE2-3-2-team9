package com.team9.anicare.domain.animal.service;

import com.team9.anicare.domain.animal.dto.FindBreedDTO;
import com.team9.anicare.domain.animal.dto.FindSpeciesDTO;
import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.domain.animal.dto.CreateAnimalDTO;
import com.team9.anicare.domain.animal.model.Breed;
import com.team9.anicare.domain.animal.model.Species;
import com.team9.anicare.domain.animal.repository.BreedRepository;
import com.team9.anicare.domain.animal.repository.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final ModelMapper modelMapper;

    public void createAnimal(CreateAnimalDTO createAnimalDTO) {

        Species species = speciesRepository.findByName(createAnimalDTO.getSpeciesName());

        // 만약 품종을 입력했다면
        if(createAnimalDTO.getBreedName() != null) {
            if (species != null) {
                // 이미 종과 품종 존재
                if (breedRepository.existsByNameAndSpecies(createAnimalDTO.getBreedName(), species)) {
                    throw new CustomException(ResultCode.DUPLICATE_SPECIES_AND_BREED);
                } else { // 종만 존재 -> 품종만 추가
                    Breed breed = new Breed();
                    breed.setSpecies(species);
                    breed.setName(createAnimalDTO.getBreedName());
                    breedRepository.save(breed);
                }
            } else { // 모두 존재X -> 종, 품종 추가
                species = new Species();
                species.setName(createAnimalDTO.getSpeciesName());
                speciesRepository.save(species);

                Breed breed = new Breed();
                breed.setSpecies(species);
                breed.setName(createAnimalDTO.getBreedName());
                breedRepository.save(breed);
            }
        }
        // 종만 입력했다면
        else {
            if(species == null) {
                species = new Species();
                species.setName(createAnimalDTO.getSpeciesName());
                speciesRepository.save(species);
            }
            else {
                throw new CustomException(ResultCode.DUPLICATE_SPECIES);
            }
        }
    }

    public List<FindSpeciesDTO> findSpecies() {
        List<Species> lists = speciesRepository.findAll();

        if (lists.isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_SPECIES);
        }

        List<FindSpeciesDTO> findSpeciesDTOs = lists.stream()
                .map(species -> modelMapper.map(species, FindSpeciesDTO.class))
                .collect(Collectors.toList());

        return findSpeciesDTOs;
    }

    public List<FindBreedDTO> findBreedsBySpecies(Long speciesId) {
        Species species = speciesRepository.findById(speciesId)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_SPECIES));

        List<Breed> lists = breedRepository.findBreedsBySpecies(species);
        if (lists.isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_BREED);
        }

        List<FindBreedDTO> findBreedDTOs = lists.stream()
                .map(breed -> modelMapper.map(breed, FindBreedDTO.class))
                .collect(Collectors.toList());

        return findBreedDTOs;
    }
}
