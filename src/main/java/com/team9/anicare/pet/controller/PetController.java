package com.team9.anicare.pet.controller;

import com.team9.anicare.auth.security.CustomUserDetails;
import com.team9.anicare.common.response.Result;
import com.team9.anicare.pet.dto.PetDTO;
import com.team9.anicare.pet.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api")
@RestController
public class PetController {
    @Autowired
    private PetService petService;

    @GetMapping("/pets")
    public ResponseEntity<List<PetDTO>> findPets() {
        Long userId = 1L;
        List<PetDTO> petDTOs = petService.findPets(userId);
        return ResponseEntity.status(HttpStatus.OK).body(petDTOs);
    }

    @PostMapping("/pet")
    public ResponseEntity<PetDTO> addPets(@RequestPart(value = "dto") PetDTO.AddPetDTO request,
                                          @RequestPart(value = "file",required = false) MultipartFile file) {
        Long userId = 1L;
        PetDTO petDTO = petService.addPet(request, userId, file);
        return ResponseEntity.status(HttpStatus.OK).body(petDTO);
    }

    @PutMapping("/pet/{petId}")
    public ResponseEntity<PetDTO> updatePets(@RequestPart(value = "dto") PetDTO.UpdatePetDTO request,
                                             @RequestPart(value = "file",required = false) MultipartFile file) {
        Long userId = 1L;
        PetDTO petDTO = petService.updatePet(request, userId, file);
        return ResponseEntity.status(HttpStatus.OK).body(petDTO);
    }

    @DeleteMapping("/pet/{petId}")
    public void deletePets(@RequestParam Long petId) {
        petService.deletePet(petId);
    }
}

