package com.team9.anicare.pet.service;

import com.team9.anicare.common.Result;
import com.team9.anicare.common.ResultCode;
import com.team9.anicare.pet.dto.PetDTO;
import com.team9.anicare.pet.model.Pet;
import com.team9.anicare.pet.repository.PetRepository;
import com.team9.anicare.species.repository.SpeciesRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PetService {
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;
    private final SpeciesRepository speciesRepository;
    private final RestTemplateAutoConfiguration restTemplateAutoConfiguration;

    public Result findPets(Long userId) {
        try {
            List<Pet> lists = petRepository.findAllByUserId(userId);

            if (lists.isEmpty()) {
                return new Result(ResultCode.NOT_EXISTS_PET);
            }

            List<PetDTO> petDTOs = lists.stream()
                    .map(pet -> modelMapper.map(pet, PetDTO.class))
                    .collect(Collectors.toList());

            return new Result(ResultCode.SUCCESS, petDTOs);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.ETC_ERROR);
        }
    }

    public Result addPet(PetDTO.AddPetDTO request, Long userId) {
        try {
            if (request.getName() == null) {
                return new Result(ResultCode.MISSING_PARAMETER);
            }
            if (!request.getGender().equals("암컷") && !request.getGender().equals("수컷")) {
                return new Result(ResultCode.INVALID_GENDER_VALUE);
            }
            if (speciesRepository.findById(request.getSpeciesId()).isEmpty()) {
                return new Result(ResultCode.NOT_EXISTS_SPECIES);
            }

            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

            Pet pet = modelMapper.map(request, Pet.class);
            pet.setUserId(userId);

            petRepository.save(pet);
            return new Result(ResultCode.SUCCESS);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.ETC_ERROR);
        }
    }

    public Result updatePet(PetDTO.UpdatePetDTO request, Long userId) {
        try {
            if(petRepository.findById(request.getId()).isEmpty()) {
                return new Result(ResultCode.NOT_EXISTS_PET);
            }
            if (request.getName() == null) {
                return new Result(ResultCode.MISSING_PARAMETER);
            }
            if (!request.getGender().equals("암컷") && !request.getGender().equals("수컷")) {
                return new Result(ResultCode.INVALID_GENDER_VALUE);
            }
            if (speciesRepository.findById(request.getSpeciesId()).isEmpty()) {
                return new Result(ResultCode.NOT_EXISTS_SPECIES);
            }

            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

            Pet pet = modelMapper.map(request, Pet.class);
            pet.setUserId(userId);

            petRepository.save(pet);
            return new Result(ResultCode.SUCCESS);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.ETC_ERROR);
        }
    }

    public Result deletePet(Long petId) {
        try {
            if (petRepository.existsById(petId)) {
                petRepository.deleteById(petId);
            } else {
                return new Result(ResultCode.NOT_EXISTS_PET);
            }

            return new Result(ResultCode.SUCCESS);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.ETC_ERROR);
        }
    }
}
