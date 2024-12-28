package com.team9.anicare.hospital.controller;

import com.team9.anicare.hospital.model.AnimalHospital;
import com.team9.anicare.hospital.service.AnimalHospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AnimalHospitalController {

    private final AnimalHospitalService animalHospitalService;

    @GetMapping("/api/animal-hospitals")
    public String importData() {
        animalHospitalService.fetchAndSaveData();
        return "데이터 수집 및 저장 완료";
    }

    // 특정 위치에서 2km 이내의 동물병원 찾기
    @GetMapping("/api/animal-hospitals/nearby")
    public ResponseEntity<Map<String, Object>> findNearbyHospitals(
            @RequestParam double latitude,
            @RequestParam double longitude) {

        List<AnimalHospital> nearbyHospitals = animalHospitalService.findHospitalsNearLocation(latitude, longitude);

        Map<String, Object> response = new HashMap<>();
        response.put("nearbyHospitals", nearbyHospitals);

        return ResponseEntity.ok(response);
    }

    //"동물병원"을 검색할경우 동물병원 리스트가 나옴
    @GetMapping("/api/animal-hospitals/namesearch")
    public ResponseEntity<?> searchHospitals(@RequestParam(required = false) String keyword) {

        //검색어가 비어있거나 키워드가 "동물병원" 이 아닌경우
        if(!"동물병원".equals(keyword) || keyword.isEmpty()) {
            return ResponseEntity.ok(List.of()); //빈 리스트를 반환
        }

        //키워드가 동물병원인경우 동물병원을 호출
        List<AnimalHospital> searchResults = animalHospitalService.searchHospitals();
        return ResponseEntity.ok(searchResults);
    }
}
