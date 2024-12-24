package com.team9.anicare.hospital.service;

import com.team9.anicare.hospital.dto.AnimalHospitalDto;
import com.team9.anicare.hospital.model.AnimalHospital;
import com.team9.anicare.hospital.repository.AnimalHospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    // .env 파일의 환경 변수 값 주입
    @Value("${API_KEY}")
    private String apiKey;

    @Value("${API_URI}")
    private String apiUri;

    /**
     * 서울시 동물병원 인허가 OpenAPI를 호출하고, 결과를 DB에 저장
     */
    public void fetchAndSaveData() {
        int startIndex = 1;
        int endIndex = 1000;

        // .env 설정 값을 사용해 URL 생성
        String url = String.format("%s/%s/json/LOCALDATA_020301/%d/%d",
                apiUri, apiKey, startIndex, endIndex);

        // Spring에서 제공하는 RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // 응답을 AnimalHospitalApiResponse 구조로 맵핑
        AnimalHospitalApiResponse response = restTemplate.getForObject(url, AnimalHospitalApiResponse.class);

        if (response != null && response.getLocaldata020301() != null) {
            // 최상위 객체 안에 row 리스트가 있다고 가정
            List<AnimalHospitalDto> rows = response.getLocaldata020301().getRow();
            if (rows != null) {
                for (AnimalHospitalDto dto : rows) {
                    if ("폐업".equals(dto.getTrdStateNm())) {
                        System.out.println("폐업 병원 제외: " + dto.getBplcNm());
                        continue; // 저장하지 않고 다음 병원으로 넘어감
                    } else if ("취소/말소/만료/정지/중지".equals(dto.getTrdStateNm())) {
                        System.out.println("취소/말소/만료/정지/중지 병원 제외: " + dto.getBplcNm());
                        continue; // 저장하지 않고 다음 병원으로 넘어감
                    } else if ("".equals(dto.getLatitude())) {
                        System.out.println("위도가 없습니다 : " + dto.getLatitude());
                        continue; // 저장하지 않고 다음 병원으로 넘어감
                    } else if ("".equals(dto.getLongitude())) {
                        System.out.println("경도가 없습니다 : " + dto.getLongitude());
                        continue; // 저장하지 않고 다음 병원으로 넘어감
                    }
                    // DTO → Entity
                    AnimalHospital entity = dto.toEntity();
                    // JPA 저장 (PK 중복이면 update됨)
                    animalHospitalRepository.save(entity);
                }
            }
        }
    }
}
