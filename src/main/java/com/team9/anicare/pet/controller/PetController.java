package com.team9.anicare.pet.controller;

import com.team9.anicare.common.Result;
import com.team9.anicare.pet.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class PetController {
    @Autowired
    private PetService petService;

    @GetMapping("/pet")
    public Result findPets(@RequestParam Long userId) {
        return petService.findPets(userId);
    }
}
