package com.team9.anicare.pet.controller;

import com.team9.anicare.common.Result;
import com.team9.anicare.pet.dto.PetDTO;
import com.team9.anicare.pet.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
public class PetController {
    @Autowired
    private PetService petService;

    @GetMapping("/pets")
    public Result findPets(@RequestParam Long userId) {
        return petService.findPets(userId);
    }

    @PostMapping("/pet")
    public Result addPets(@RequestBody PetDTO.AddPetDTO request, @RequestParam Long userId) {
        return petService.addPet(request, userId);
    }

    @PutMapping("/pet/{petId}")
    public Result addPets(@RequestBody PetDTO.UpdatePetDTO request, @RequestParam Long userId) {
        return petService.updatePet(request, userId);
    }

    @DeleteMapping("/pet/{petId}")
    public Result deletePets(@RequestParam Long petId) {
        return petService.deletePet(petId);
    }
}
