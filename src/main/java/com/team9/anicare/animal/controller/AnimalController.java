package com.team9.anicare.animal.controller;

import com.team9.anicare.animal.dto.CreateAnimalDTO;
import com.team9.anicare.animal.dto.FindBreedDTO;
import com.team9.anicare.animal.dto.FindSpeciesDTO;
import com.team9.anicare.animal.model.Species;
import com.team9.anicare.animal.service.AnimalService;
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

    @PostMapping("/animal")
    public ResponseEntity<Void> createAnimal(@RequestBody CreateAnimalDTO createAnimalDTO) {
        animalService.createAnimal(createAnimalDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/species")
    public ResponseEntity<List<FindSpeciesDTO>> findSpecies() {
        List<FindSpeciesDTO> findSpeciesDTOs = animalService.findSpecies();
        return ResponseEntity.status(HttpStatus.OK).body(findSpeciesDTOs);
    }

    @GetMapping("/breeds/{speciesId}")
    public ResponseEntity<List<FindBreedDTO>> findBreeds(@PathVariable Long speciesId) {
       List<FindBreedDTO> findBreedDTOs = animalService.findBreedsBySpecies(speciesId);
        return ResponseEntity.status(HttpStatus.OK).body(findBreedDTOs);
    }
}