package com.team9.anicare.domain.hospital.controller;

import com.team9.anicare.domain.hospital.dto.AnimalHospitalDto;
import com.team9.anicare.domain.hospital.model.AnimalHospital;
import com.team9.anicare.domain.hospital.service.AnimalHospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Tag(name = "animal hospital", description = "동물병원 API")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class AnimalHospitalController {

    private final AnimalHospitalService animalHospitalService;
    @Operation(summary = "공공 API 호출",
            description = "공공 API 데이터를 받아옵니다.")
    @GetMapping("/api/animal-hospitals")
    public String importData() {
        animalHospitalService.fetchAndSaveData();
        return "데이터 수집 및 저장 완료";
    }
    @Operation(summary = "좌표 변환",
            description = "좌표를 변환합니다. EPSG:5179 형식(국내에서 흔히 사용되는 TM 좌표계)일 경우, 이를 EPSG:4326 형식(세계 표준 위경도 좌표계)")
    @PostMapping("/api/update-coordinates")
    public String updateCoordinates() {
        animalHospitalService.fetchAndSaveCoordinates();
        return "좌표수정";
    }



    // 특정 위치에서 2km 이내의 동물병원 찾기
    @Operation(summary = "주변 동물병원 검색",
            description = "주변 동물병원 검색을 하는 API 입니다. 요청 항목 : latitude, longitude")
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
    @Operation(summary = "동물병원 단어 검색",
            description = "동물병원을 검색 하는 API 입니다. 요청 항목: keyword 동물병원")
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

    //  주소 검색 로직
    @Operation(summary = "동물병원 주소 동, 구, 키워드 검색",
            description = "동물병원 주소 동, 구, 키워드 검색 API 입니다. 요청 항목 : **동(dong), **구(gu), 키워드(keyword)")
    @GetMapping("/api/animal-hospitals/dongorgu")
    public List<AnimalHospitalDto> searchHospitalsByDongOrGu(
            @RequestParam(required = false) String gu,
            @RequestParam(required = false) String dong,
            @RequestParam(required = false) String keyword) {
        // `gu`와 `dong`이 있을 경우 구와 동으로 검색
        if (gu != null || dong != null) {
            return animalHospitalService.searchHospitalsByGuAndDong(gu, dong);
        }

        // 키워드 검색
        if (keyword != null && !keyword.isBlank()) {
            return animalHospitalService.searchHospitalsByKeyword(keyword);
        }

        // 모든 조건이 없을 경우 빈 리스트 반환
        return List.of();
    }
}
