package com.team9.anicare.information.controller;

import com.team9.anicare.common.dto.PageDTO;
import com.team9.anicare.common.dto.PageRequestDTO;
import com.team9.anicare.information.dto.InformationRequestDTO;
import com.team9.anicare.information.dto.InformationResponseDTO;
import com.team9.anicare.information.service.InformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/api/information")
@RequiredArgsConstructor
public class InformationController {

    private final InformationService informationService;

    @PostMapping
    public ResponseEntity<Void> saveInformation(
            @RequestPart(value = "dto") InformationRequestDTO informationDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        informationService.saveInformation(informationDTO, file);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getInformation(
            PageRequestDTO pageRequestDTO,
            @RequestParam(required = false) String speciesName,
            @RequestParam(required = false) String breedName) {

        PageDTO<InformationResponseDTO> pageDTO = informationService.getInformation(pageRequestDTO, speciesName, breedName);

        return ResponseEntity.ok(pageDTO);
    }

    @GetMapping("/{informationId}")
    public ResponseEntity<InformationResponseDTO> getInformationDetail(@PathVariable Long informationId) {
        InformationResponseDTO informationDTO = informationService.getInformationDetail(informationId);

        return ResponseEntity.ok(informationDTO);
    }
}
