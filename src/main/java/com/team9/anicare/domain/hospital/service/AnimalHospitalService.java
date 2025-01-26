package com.team9.anicare.domain.hospital.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.anicare.domain.hospital.dto.AnimalHospitalDto;
import com.team9.anicare.domain.hospital.repository.AnimalHospitalRepository;
import com.team9.anicare.domain.hospital.model.AnimalHospital;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalHospitalService {

    private final AnimalHospitalRepository animalHospitalRepository;
    private final CoordinateConverter coordinateConverter;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;

    @Value("${API_KEY}")
    private String apiKey;

    @Value("${API_URI}")
    private String apiUri;



    @Transactional(readOnly = true)
    public List<AnimalHospitalDto> searchHospitalsByGuAndDong(String gu, String dong) {
        // `gu`와 `dong`을 조건에 따라 검색
        List<AnimalHospital> hospitals;
        if (gu != null && dong != null) {
            hospitals = animalHospitalRepository.findByRdnWhlAddrContainingAndSiteWhlAddrContaining(gu, dong);
        } else if (gu != null) {
            hospitals = animalHospitalRepository.findByRdnWhlAddrContaining(gu);
        } else {
            hospitals = animalHospitalRepository.findBySiteWhlAddrContaining(dong);
        }

        // DTO로 변환 후 반환
        return hospitals.stream()
                .map(hospital -> AnimalHospitalDto.builder()
                        .opnsfTeamCode(hospital.getOpnsfTeamCode())
                        .mgtNo(hospital.getMgtNo())
                        .apvPermYmd(hospital.getApvPermYmd())
                        .trdStateGbn(hospital.getTrdStateGbn())
                        .trdStateNm(hospital.getTrdStateNm())
                        .siteTel(hospital.getSiteTel())
                        .siteWhlAddr(hospital.getSiteWhlAddr())
                        .rdnWhlAddr(hospital.getRdnWhlAddr())
                        .bplcNm(hospital.getBplcNm())
                        .uptaeNm(hospital.getUptaeNm())
                        .xCode(hospital.getXCode())
                        .yCode(hospital.getYCode())
                        .latitude(hospital.getLatitude())
                        .longitude(hospital.getLongitude())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnimalHospitalDto> searchHospitalsByKeyword(String keyword) {
        List<AnimalHospital> hospitals = animalHospitalRepository.findByRdnWhlAddrContainingOrSiteWhlAddrContaining(keyword, keyword);

        return hospitals.stream()
                .map(hospital -> AnimalHospitalDto.builder()
                        .opnsfTeamCode(hospital.getOpnsfTeamCode())
                        .mgtNo(hospital.getMgtNo())
                        .apvPermYmd(hospital.getApvPermYmd())
                        .trdStateGbn(hospital.getTrdStateGbn())
                        .trdStateNm(hospital.getTrdStateNm())
                        .siteTel(hospital.getSiteTel())
                        .siteWhlAddr(hospital.getSiteWhlAddr())
                        .rdnWhlAddr(hospital.getRdnWhlAddr())
                        .bplcNm(hospital.getBplcNm())
                        .uptaeNm(hospital.getUptaeNm())
                        .xCode(hospital.getXCode())
                        .yCode(hospital.getYCode())
                        .latitude(hospital.getLatitude())
                        .longitude(hospital.getLongitude())
                        .build())
                .collect(Collectors.toList());
    }

    public void fetchAndSaveCoordinates() {
        // 모든 병원 데이터를 가져옵니다.
        List<AnimalHospital> hospitals = animalHospitalRepository.findAll();

        for (AnimalHospital hospital : hospitals) {
            String address = hospital.getRdnWhlAddr();
            if (address == null || address.isEmpty()) {
                continue; // 주소가 없는 경우 스킵
            }

            // Kakao Local API 호출
            String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoClientId);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

                // Kakao API 응답 파싱
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode documents = root.get("documents");

                if (documents != null && documents.size() > 0) {
                    JsonNode location = documents.get(0);
                    double latitude = location.get("y").asDouble();
                    double longitude = location.get("x").asDouble();

                    // 엔티티에 좌표 저장
                    hospital.setLatitude(latitude);
                    hospital.setLongitude(longitude);

                    // 데이터베이스에 저장 (로깅 제거)
                    animalHospitalRepository.save(hospital);
                } else {
                    // 좌표를 찾을 수 없는 주소만 출력
                    System.err.println("좌표를 찾을 수 없는 주소: " + address);
                }
            } catch (Exception e) {
                // 오류 발생 시만 출력
                System.err.println("좌표를 가져오는 중 오류 발생: " + e.getMessage());
            }
        }
    }

    private String preprocessAddress(String address) {
        if (address == null || address.isEmpty()) {
            return ""; // 주소가 없거나 비어 있는 경우 빈 문자열 반환
        }

        // 쉼표로 나누고 첫 번째 부분만 사용 (쉼표가 없으면 전체 주소 사용)
        String simplifiedAddress = address.contains(",") ? address.split(",")[0] : address;

        // 공백 및 특수문자 제거
        return simplifiedAddress.replaceAll("[^a-zA-Z0-9가-힣\\s]", "").trim();
    }

    public void fetchAndSaveData() {
        String url = String.format("%s/%s/json/LOCALDATA_020301/1/1000", apiUri, apiKey);

        RestTemplate restTemplate = new RestTemplate();
        AnimalHospitalApiResponse response = restTemplate.getForObject(url, AnimalHospitalApiResponse.class);

        if (response != null && response.getLocaldata020301() != null) {
            List<AnimalHospitalDto> rows = response.getLocaldata020301().getRow();
            rows.stream()
                    .filter(this::isValidHospital) // 병원 상태 및 좌표 필터링
                    .forEach(dto -> {
                        try {
                            double x = Double.parseDouble(dto.getXCode());
                            double y = Double.parseDouble(dto.getYCode());
                            double[] latLon = coordinateConverter.convertEPSG5179ToWGS84(x, y);

                            dto.setLatitude(latLon[0]);
                            dto.setLongitude(latLon[1]);

                            AnimalHospital entity = dto.toEntity();
                            animalHospitalRepository.save(entity);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    // 유효한 병원인지 확인하는 메서드
    private boolean isValidHospital(AnimalHospitalDto dto) {
        // 1. 병원 상태 필터링
        String state = dto.getTrdStateNm();
        if (state == null || state.matches(".*(폐업|취소|말소|만료|정지|중지).*")) {
            return false; // 유효하지 않은 상태인 경우 제외
        }

        // 2. 좌표 필터링
        if (dto.getXCode() == null || dto.getXCode().trim().isEmpty() ||
                dto.getYCode() == null || dto.getYCode().trim().isEmpty()) {
            return false; // 좌표가 없는 경우 제외
        }

        return true; // 유효한 병원
    }

    // "동물병원"으로 키워드 고정해서 검색
    public List<AnimalHospital> searchHospitals() {
        return animalHospitalRepository.findByBplcNmContaining("동물병원");

    }

    public List<AnimalHospital> findHospitalsNearLocation(double latitude, double longitude) {
        // 모든 병원을 조회하고 필터링
        List<AnimalHospital> nearbyHospitals = animalHospitalRepository.findAll().stream()
                .filter(hospital -> {
                    try {
                        // 위경도 값 확인 및 거리 계산
                        double hospitalLat = hospital.getLatitude();
                        double hospitalLon = hospital.getLongitude();
                        double distance = calculateDistance(latitude, longitude, hospitalLat, hospitalLon);
                        return distance <= 3.0; // 반경 5km 이내인지 확인
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false; // 좌표가 유효하지 않으면 제외
                    }
                })
                .collect(Collectors.toList());

        // 결과 데이터의 갯수를 콘솔에 출력
        System.out.println("Number of hospitals within 5 km: " + nearbyHospitals.size());
        return nearbyHospitals;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS = 6371; // 지구 반지름 (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c; // 거리 반환
    }

    // 주소에서 우편번호 추출 (예시: 우편번호가 주소에 포함되어 있을 경우)
    private String extractZipCode(String rdnWhlAddr) {
        if (rdnWhlAddr == null || !rdnWhlAddr.matches(".*\\d{5}.*")) {
            return "우편번호 없음";
        }
        return rdnWhlAddr.replaceAll(".*(\\d{5}).*", "$1");
    }

    // 동 검색: 일반 주소 반환
    public List<AnimalHospital> searchByRdnWhlAddr(String address) {
        return animalHospitalRepository.findByRdnWhlAddrContaining(address);
    }

    // 구 검색: 도로명 주소 반환
    public List<AnimalHospital> searchBySiteWhlAddr(String address) {
        return animalHospitalRepository.findBySiteWhlAddrContaining(address);
    }

}
