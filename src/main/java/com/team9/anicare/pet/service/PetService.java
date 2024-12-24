package com.team9.anicare.pet.service;

import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.pet.dto.PetDTO;
import com.team9.anicare.pet.model.Pet;
import com.team9.anicare.pet.repository.PetRepository;
import com.team9.anicare.species.repository.SpeciesRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PetService {
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;
    private final SpeciesRepository speciesRepository;

    public List<PetDTO> findPets(Long userId) {
        List<Pet> lists = petRepository.findAllByUserId(userId);

        if (lists.isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        }

        List<PetDTO> petDTOs = lists.stream()
                .map(pet -> modelMapper.map(pet, PetDTO.class))
                .collect(Collectors.toList());

        return petDTOs;
    }

    public PetDTO addPet(PetDTO.AddPetDTO request, Long userId) {
        if (request.getName() == null) {
            throw new CustomException(ResultCode.MISSING_PARAMETER);
        }
        if (!request.getGender().equals("암컷") && !request.getGender().equals("수컷")) {
            throw new CustomException(ResultCode.INVALID_GENDER_VALUE);
        }
        if (speciesRepository.findById(request.getSpeciesId()).isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_SPECIES);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Pet pet = modelMapper.map(request, Pet.class);
        pet.setUserId(userId);

        petRepository.save(pet);
        PetDTO petDTO = modelMapper.map(pet,PetDTO.class);
        return petDTO;
    }

    public PetDTO updatePet(PetDTO.UpdatePetDTO request, Long userId) {
        if (petRepository.findById(request.getId()).isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        }
        if (request.getName() == null) {
            throw new CustomException(ResultCode.MISSING_PARAMETER);
        }
        if (!request.getGender().equals("암컷") && !request.getGender().equals("수컷")) {
            throw new CustomException(ResultCode.INVALID_GENDER_VALUE);
        }
        if (speciesRepository.findById(request.getSpeciesId()).isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_SPECIES);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Pet pet = modelMapper.map(request, Pet.class);
        pet.setUserId(userId);

        petRepository.save(pet);
        PetDTO petDTO = modelMapper.map(pet,PetDTO.class);
        return petDTO;
    }

    public String deletePet(Long petId) {
        if (petRepository.existsById(petId)) {
            petRepository.deleteById(petId);
        } else {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        }
        return "반려동물이 삭제되었습니다";
    }
}
