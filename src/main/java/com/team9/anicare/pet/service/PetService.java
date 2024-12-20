package com.team9.anicare.pet.service;

import com.team9.anicare.common.Result;
import com.team9.anicare.common.ResultCode;
import com.team9.anicare.pet.dto.PetDTO;
import com.team9.anicare.pet.model.Pet;
import com.team9.anicare.pet.repository.PetRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PetService {
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;
    public Result findPets(Long userId) {
        List<Pet> lists = petRepository.findAllByUserId(userId);

        if(lists.isEmpty()) {
            return new Result(ResultCode.NOT_EXISTS_PET);
        }

        List<PetDTO> petDTOs = lists.stream()
                .map(pet -> modelMapper.map(pet, PetDTO.class))
                .collect(Collectors.toList());

        return new Result(ResultCode.SUCCESS,petDTOs);
    }
}
