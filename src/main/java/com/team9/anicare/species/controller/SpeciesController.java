package com.team9.anicare.species.controller;

import com.team9.anicare.species.dto.CreateAnimalDTO;
import com.team9.anicare.species.service.SpeciesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class SpeciesController {

    private final SpeciesService speciesService;

    @PostMapping("/animal")
    public ResponseEntity<Void> createAnimal(@RequestBody CreateAnimalDTO createAnimalDTO) {
        speciesService.createAnimal(createAnimalDTO);

        return ResponseEntity.noContent().build();
    }

}