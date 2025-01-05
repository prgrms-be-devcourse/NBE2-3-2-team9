package com.team9.anicare.domain.animal.controller;

import com.team9.anicare.domain.animal.dto.CreateAnimalDTO;
import com.team9.anicare.domain.animal.dto.FindBreedDTO;
import com.team9.anicare.domain.animal.dto.FindSpeciesDTO;
import com.team9.anicare.domain.animal.service.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class AnimalController {

    private final AnimalService animalService;

    @Operation(summary = "종, 품종 생성", description = "종, 품종 생성 API 입니다." )
    @PostMapping("/animal")
    public ResponseEntity<Void> createAnimal(@RequestBody CreateAnimalDTO createAnimalDTO) {
        animalService.createAnimal(createAnimalDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "종 조회", description = "종 조회 API 입니다." )
    @GetMapping("/species")
    public ResponseEntity<List<FindSpeciesDTO>> findSpecies() {
        List<FindSpeciesDTO> findSpeciesDTOs = animalService.findSpecies();
        return ResponseEntity.status(HttpStatus.OK).body(findSpeciesDTOs);
    }

    @Operation(summary = "품종 조회", description = "품종 조회 API 입니다. 종에 대한 품종 조회이므로 speciesId를 입력해야 합니다" )
    @GetMapping("/breeds/{speciesId}")
    public ResponseEntity<List<FindBreedDTO>> findBreeds(@PathVariable Long speciesId) {
       List<FindBreedDTO> findBreedDTOs = animalService.findBreedsBySpecies(speciesId);
        return ResponseEntity.status(HttpStatus.OK).body(findBreedDTOs);
    }
}