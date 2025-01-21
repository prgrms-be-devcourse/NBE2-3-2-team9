package com.team9.anicare.domain.pet.service;

import com.team9.anicare.domain.animal.model.Breed;
import com.team9.anicare.domain.animal.model.Species;
import com.team9.anicare.domain.animal.repository.BreedRepository;
import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.common.file.service.S3FileService;
import com.team9.anicare.domain.pet.dto.PetDTO;
import com.team9.anicare.domain.pet.repository.PetRepository;
import com.team9.anicare.domain.pet.model.Pet;
import com.team9.anicare.domain.animal.repository.SpeciesRepository;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final BreedRepository breedRepository;
    private final S3FileService s3FileService;

    public List<PetDTO> findPets(Long userId) {
        List<Pet> lists = petRepository.findAllByUserId(userId);

        if (lists.isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        }

        List<PetDTO> petDTOs = lists.stream()
                .map(pet -> PetDTO.builder()
                        .id(pet.getId())
                        .userId(pet.getUser().getId())
                        .speciesId(pet.getSpecies().getId())
                        .breedId(pet.getBreed().getId())
                        .name(pet.getName())
                        .age(pet.getAge())
                        .picture(pet.getPicture())
                        .gender(pet.getGender())
                        .speciesName(pet.getSpecies().getName())
                        .breedName(pet.getBreed().getName())
                        .createdAt(pet.getCreatedAt())
                        .updatedAt(pet.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return petDTOs;
    }

    public PetDTO getPetDetails(Long userId, Long petId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ResultCode.NOT_EXISTS_USER);
        }
        if (!petRepository.existsById(petId)) {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        }
        Pet pet = petRepository.findById(petId).orElseThrow(RuntimeException::new);

        PetDTO petDTO = PetDTO.builder()
                .id(pet.getId())
                .breedId(pet.getBreed().getId())
                .speciesId(pet.getSpecies().getId())
                .picture(pet.getPicture())
                .userId(pet.getUser().getId())
                .age(pet.getAge())
                .name(pet.getName())
                .gender(pet.getGender())
                .breedName(pet.getBreed().getName())
                .speciesName(pet.getSpecies().getName())
                .createdAt(pet.getCreatedAt())
                .updatedAt(pet.getUpdatedAt())
                .build();

        return petDTO;
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

        String Picture = null;
        try {
            if (file != null && !file.isEmpty()) {
                Picture = s3FileService.updateFile(file, null, "pet");
            }
        } catch (IOException e) {
            throw new CustomException(ResultCode.FILE_UPLOAD_ERROR);
        }

        Pet pet = Pet.builder()
                .breed(getBreedById(request.getBreedId()))
                .species(getSpeciesById(request.getSpeciesId()))
                .picture(Picture)
                .user(getUserById(userId))
                .age(request.getAge())
                .name(request.getName())
                .gender(request.getGender())
                .build();

        petRepository.save(pet);

        PetDTO petDTO = PetDTO.builder()
                .id(pet.getId())
                .breedId(pet.getBreed().getId())
                .speciesId(pet.getSpecies().getId())
                .picture(pet.getPicture())
                .userId(pet.getUser().getId())
                .age(pet.getAge())
                .name(pet.getName())
                .gender(pet.getGender())
                .createdAt(pet.getCreatedAt())
                .updatedAt(pet.getUpdatedAt())
                .build();

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
        String Picture = null;
        try {
            if (file != null && !file.isEmpty()) {
                Picture = (s3FileService.updateFile(file, pet.getPicture(), "pet"));
            } else if (pet.getPicture() != null) {
                Picture = (pet.getPicture());
            }
        } catch (IOException e) {
            throw new CustomException(ResultCode.FILE_UPLOAD_ERROR);
        }

        pet.updatePet(request,speciesRepository,breedRepository,Picture);

        petRepository.save(pet);

        PetDTO petDTO = PetDTO.builder()
                .id(pet.getId())
                .breedId(pet.getBreed().getId())
                .speciesId(pet.getSpecies().getId())
                .picture(pet.getPicture())
                .userId(pet.getUser().getId())
                .age(pet.getAge())
                .name(pet.getName())
                .gender(pet.getGender())
                .createdAt(pet.getCreatedAt())
                .updatedAt(pet.getUpdatedAt())
                .build();

        return petDTO;
    }

    public void deletePet(Long petId) {
        if (petRepository.existsById(petId)) {
            petRepository.deleteById(petId);
        } else {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(RuntimeException::new);
    }

    private Species getSpeciesById(Long speciesId) {
        return speciesRepository.findById(speciesId).orElseThrow(RuntimeException::new);
    }

    private Breed getBreedById(Long breedId) {
        return breedRepository.findById(breedId).orElseThrow(RuntimeException::new);
    }
}
