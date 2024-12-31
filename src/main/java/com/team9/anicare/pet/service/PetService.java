package com.team9.anicare.pet.service;

import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.file.service.S3FileService;
import com.team9.anicare.pet.dto.PetDTO;
import com.team9.anicare.pet.model.Pet;
import com.team9.anicare.pet.repository.PetRepository;
import com.team9.anicare.animal.repository.SpeciesRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PetService {
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;
    private final SpeciesRepository speciesRepository;
    private final S3FileService s3FileService;

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

    public PetDTO addPet(PetDTO.AddPetDTO request, Long userId, MultipartFile file) {
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

        try {
            if (file != null && !file.isEmpty()) {
                pet.setPicture(s3FileService.updateFile(file, pet.getPicture(), "pet"));
            }
        } catch (IOException e) {
            throw new CustomException(ResultCode.FILE_UPLOAD_ERROR);
        }
        pet.setUserId(userId);

        petRepository.save(pet);
        PetDTO petDTO = modelMapper.map(pet,PetDTO.class);
        return petDTO;
    }

    public PetDTO updatePet(PetDTO.UpdatePetDTO request, Long userId, MultipartFile file) {
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

        Pet pet = petRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_PET));
        pet.setUserId(userId);
        pet.setSpeciesId(request.getSpeciesId());
        pet.setName(request.getName());
        pet.setAge(request.getAge());
        pet.setGender(request.getGender());

        try {
            if (file != null && !file.isEmpty()) {
                pet.setPicture(s3FileService.updateFile(file, pet.getPicture(), "pet"));
            } else if (pet.getPicture() != null) {
                pet.setPicture(pet.getPicture());
            }
        } catch (IOException e) {
            throw new CustomException(ResultCode.FILE_UPLOAD_ERROR);
        }

        petRepository.save(pet);
        PetDTO petDTO = modelMapper.map(pet,PetDTO.class);
        return petDTO;
    }

    public void deletePet(Long petId) {
        if (petRepository.existsById(petId)) {
            petRepository.deleteById(petId);
        } else {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        }
    }
}
