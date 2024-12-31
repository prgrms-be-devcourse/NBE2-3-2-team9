package com.team9.anicare.animal.service;

import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.animal.dto.CreateAnimalDTO;
import com.team9.anicare.animal.model.Breed;
import com.team9.anicare.animal.model.Species;
import com.team9.anicare.animal.repository.BreedRepository;
import com.team9.anicare.animal.repository.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpeciesService {

    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;

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

}
