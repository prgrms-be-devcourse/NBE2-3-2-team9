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

import java.util.List;

@RequestMapping("/api")
@RestController
public class PetController {
    @Autowired
    private PetService petService;

    @GetMapping("/pets")
    public ResponseEntity<List<PetDTO>> findPets(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PetDTO> petDTOs = petService.findPets(userDetails  .getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(petDTOs);
    }


//    @GetMapping("/pets")
//    public ResponseEntity<List<PetDTO>> findPets() {
//        Long userId = 1L;
//        List<PetDTO> petDTOs = petService.findPets(userId);
//        return ResponseEntity.status(HttpStatus.OK).body(petDTOs);
//    }

    @PostMapping("/pet")
    public ResponseEntity<PetDTO> addPets(@RequestBody PetDTO.AddPetDTO request,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        PetDTO petDTO = petService.addPet(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(petDTO);
    }



    @PutMapping("/pet/{petId}")
    public ResponseEntity<PetDTO> updatePets(@RequestBody PetDTO.UpdatePetDTO request,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        PetDTO petDTO = petService.updatePet(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(petDTO);
    }

    @DeleteMapping("/pet/{petId}")
    public ResponseEntity<String> deletePets(@RequestParam Long petId) {
        String result = petService.deletePet(petId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
