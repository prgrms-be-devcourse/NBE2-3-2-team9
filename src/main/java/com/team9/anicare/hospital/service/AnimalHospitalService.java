package com.team9.anicare.hospital.service;

import com.team9.anicare.hospital.dto.AnimalHospitalDto;
import com.team9.anicare.hospital.model.AnimalHospital;
import com.team9.anicare.hospital.repository.AnimalHospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * OpenAPI 호출 및 DB 저장 처리
 */
@Service
@RequiredArgsConstructor
public class AnimalHospitalService {

    private final AnimalHospitalRepository animalHospitalRepository;

    /**
     * 서울시 종로구 동물병원 인허가 OpenAPI를 호출하고, 결과를 DB에 저장
     *
     * 동물병원 60개밖에없어요 ㅠ
     */
    public void fetchAndSaveData( ) {
        // 예시 URL (json 형태를 요청)
        // http://openAPI.seoul.go.kr:8088/{KEY}/json/LOCALDATA_020301_JN/{START_INDEX}/{END_INDEX}
        // ex) http://openAPI.seoul.go.kr:8088/인증키/json/LOCALDATA_020301_DJ/1/100
        String serviceKey = "4265674374746c733734684b53464f";
        int startIndex = 1;
        int endIndex = 1000;
        String url = String.format(
                "http://openAPI.seoul.go.kr:8088/%s/json/LOCALDATA_020301_DJ/%d/%d",
                serviceKey, startIndex, endIndex
        );

        // Spring에서 제공하는 RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // 응답을 AnimalHospitalApiResponse 구조로 맵핑
        AnimalHospitalApiResponse response
                = restTemplate.getForObject(url, AnimalHospitalApiResponse.class);

        if (response != null && response.getLocaldata020301Dj() != null) {
            // 최상위 객체 안에 row 리스트가 있다고 가정
            List<AnimalHospitalDto> rows = response.getLocaldata020301Dj().getRow();
            if (rows != null) {
                for (AnimalHospitalDto dto : rows) {
                    // DTO → Entity
                    AnimalHospital entity = dto.toEntity();
                    // JPA 저장 (PK 중복이면 update됨)
                    animalHospitalRepository.save(entity);
                }
            }
        }
    }
}