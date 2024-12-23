package com.team9.anicare.hospital.controller;

import com.team9.anicare.hospital.service.AnimalHospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AnimalHospitalController {

    private final AnimalHospitalService animalHospitalService;

    /**
     * 예시: GET /api/animal-hospitals?serviceKey=...&start=1&end=5
     * 동물병원은 60개가 끝
     *
     */
    @GetMapping("/api/animal-hospitals")
    public String importData() {
        animalHospitalService.fetchAndSaveData();
        return "데이터 수집 및 저장 완료";
    }
}