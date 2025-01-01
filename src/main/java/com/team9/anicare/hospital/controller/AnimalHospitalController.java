package com.team9.anicare.hospital.controller;

import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.common.response.Result;
import com.team9.anicare.hospital.dto.AnimalHospitalDetailsDto;
import com.team9.anicare.hospital.model.AnimalHospital;
import com.team9.anicare.hospital.service.AnimalHospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    // **동을 검색하면 일반 주소(site_whl_addr), 병원이름, 병원전화번호 반환
    // **구를 검색하면 도로명 주소(rdn_whl_addr), 병원이름, 병원전화번호 반환
    @GetMapping("/api/animal-hospitals/dongorgu")
    public ResponseEntity<?> searchDongOrGuHospitals(@RequestParam(required = false) String address) {
        if (address == null || address.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<Map<String, String>> searchResults;

        if (address.contains("동")) {
            searchResults = animalHospitalService.searchByRdnWhlAddr(address).stream()
                    .map(hospital -> {
                        Map<String, String> result = new HashMap<>();
                        result.put("address", hospital.getSiteWhlAddr()); // 일반 주소 반환
                        result.put("name", hospital.getBplcNm()); // 병원이름 반환
                        result.put("phone", hospital.getSiteTel()); // 병원 전화번호 반환
                        return result;
                    })
                    .collect(Collectors.toList());
        } else if (address.contains("구")) {
            searchResults = animalHospitalService.searchBySiteWhlAddr(address).stream()
                    .map(hospital -> {
                        Map<String, String> result = new HashMap<>();
                        result.put("address", hospital.getRdnWhlAddr()); // 도로명 주소 반환
                        result.put("name", hospital.getBplcNm()); // 병원이름 반환
                        result.put("phone", hospital.getSiteTel()); // 병원 전화번호 반환
                        return result;
                    })
                    .collect(Collectors.toList());
        } else {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(searchResults);
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
