package com.team9.anicare.hospital.controller;

import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.common.response.Result;
import com.team9.anicare.hospital.dto.AnimalHospitalDetailsDto;
import com.team9.anicare.hospital.dto.AnimalHospitalDto;
import com.team9.anicare.hospital.model.AnimalHospital;
import com.team9.anicare.hospital.service.AnimalHospitalService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping
public class AnimalHospitalController {

    private final AnimalHospitalService animalHospitalService;

    @GetMapping("/api/animal-hospitals")
    public String importData() {
        animalHospitalService.fetchAndSaveData();
        return "데이터 수집 및 저장 완료";
    }

    @PostMapping("/api/update-coordinates")
    public String updateCoordinates() {
        animalHospitalService.fetchAndSaveCoordinates();
        return "좌표수정";
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

    //  주소 검색 로직
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



    @PostMapping("/api/animal-hospitals/{mgtNo}/like")
    public ResponseEntity<Result<String>> likeHospital(
            @PathVariable String mgtNo,
            @RequestParam String userId) {
        try {
            Result<String> result = animalHospitalService.likeHospital(mgtNo, userId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Result<>(ResultCode.INVALID_REQUEST, e.getMessage()));
        }
    }

    // 동물병원 좋아요 취소
    @DeleteMapping("/api/animal-hospitals/{mgtNo}/like")
    public ResponseEntity<Result<String>> unlikeHospital(
            @PathVariable String mgtNo,
            @RequestParam String userId) {
        try {
            Result<String> result = animalHospitalService.unlikeHospital(mgtNo, userId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Result<>(ResultCode.INVALID_REQUEST, e.getMessage()));
        }
    }

    // 좋아요 수 확인 API
    @GetMapping("/api/animal-hospitals/{mgtNo}/like-count")
    public ResponseEntity<Result<Long>> getLikeCount(@PathVariable String mgtNo) {
        long likeCount = animalHospitalService.getLikeCount(mgtNo);
        return ResponseEntity.ok(new Result<>(ResultCode.SUCCESS, likeCount));
    }

    //동물병원 상세검색
    @GetMapping("/api/animal-hospitals/{mgtNo}/details")
    public ResponseEntity<Result<AnimalHospitalDetailsDto>> getHospitalDetails(@PathVariable String mgtNo) {
        AnimalHospitalDetailsDto hospitalDetails = animalHospitalService.getHospitalDetailsDto(mgtNo);
        return ResponseEntity.ok(new Result<>(ResultCode.SUCCESS, hospitalDetails));
    }
}
