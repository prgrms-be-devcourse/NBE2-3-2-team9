package com.team9.anicare.domain.pet.controller;

import com.team9.anicare.domain.auth.security.CustomUserDetails;
import com.team9.anicare.domain.pet.dto.PetDTO;
import com.team9.anicare.domain.pet.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "pet", description = "반려동물 API")
@RequestMapping("/api")
@RestController
public class PetController {
    @Autowired
    private PetService petService;

    @Operation(summary = "반려동물 조회", description = "반려동물 조회 API 입니다. 필수 요청 항목 : 로그인" )
    @GetMapping("/pets")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<PetDTO>> findPets(@AuthenticationPrincipal CustomUserDetails userDetails) {

        List<PetDTO> petDTOs = petService.findPets(userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(petDTOs);
    }

    @Operation(summary = "반려동물 등록", description = "반려동물 등록 API 입니다. 필수 요청 항목 : 로그인, breedId, speciesId, name, gender(암컷 or 수컷) / 그 외 : age, file(반려동물 사진)" )
    @PostMapping(value = "/pet", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    public ResponseEntity<PetDTO> addPets(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(value = "dto") PetDTO.AddPetDTO request,
            @RequestPart(value = "file",required = false) MultipartFile file) {
        PetDTO petDTO = petService.addPet(request, userDetails.getUserId(), file);
        return ResponseEntity.status(HttpStatus.OK).body(petDTO);
    }

    @Operation(summary = "반려동물 수정", description = "반려동물 수정 API 입니다. 필수 요청 항목 : 로그인, ID(Pet), breedId, speciesId, name, gender(암컷 or 수컷) / 그 외 : age, file(image)" )
    @PutMapping(value = "/pet/{petId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PetDTO> updatePets(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(value = "dto") PetDTO.UpdatePetDTO request,
            @RequestPart(value = "file",required = false) MultipartFile file) {
        PetDTO petDTO = petService.updatePet(request, userDetails.getUserId(), file);
        return ResponseEntity.status(HttpStatus.OK).body(petDTO);
    }

    @Operation(summary = "반려동물 삭제", description = "반려동물 삭제 API 입니다. 필수 요청 항목 : ID(Pet)" )
    @DeleteMapping("/pet/{petId}")
    public void deletePets(@PathVariable Long petId) {
        petService.deletePet(petId);
    }
}

