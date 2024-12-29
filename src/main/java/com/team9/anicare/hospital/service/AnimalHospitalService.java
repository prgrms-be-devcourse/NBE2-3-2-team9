package com.team9.anicare.hospital.service;

import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.common.response.Result;
import com.team9.anicare.hospital.dto.AnimalHospitalDetailsDto;
import com.team9.anicare.hospital.dto.AnimalHospitalDto;
import com.team9.anicare.hospital.model.AnimalHospital;
import com.team9.anicare.hospital.model.HospitalLike;
import com.team9.anicare.hospital.repository.AnimalHospitalRepository;
import com.team9.anicare.hospital.repository.HospitalLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalHospitalService {

    private final AnimalHospitalRepository animalHospitalRepository;
    private final CoordinateConverter coordinateConverter;
    private final HospitalLikeRepository hospitalLikeRepository;


    @Value("${API_KEY}")
    private String apiKey;

    @Value("${API_URI}")
    private String apiUri;

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

                            dto.setLatitude(String.valueOf(latLon[0]));
                            dto.setLongitude(String.valueOf(latLon[1]));

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
        return animalHospitalRepository.findAll().stream()
                .filter(hospital -> {
                    try {
                        // 위경도 값 확인 및 거리 계산
                        double hospitalLat = Double.parseDouble(hospital.getLatitude());
                        double hospitalLon = Double.parseDouble(hospital.getLongitude());
                        double distance = calculateDistance(latitude, longitude, hospitalLat, hospitalLon);
                        return distance <= 0.5; // 반경 2km 이내인지 확인
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false; // 좌표가 유효하지 않으면 제외
                    }
                })
                .collect(Collectors.toList());
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

    //동물병원 좋아요 로직
    @Transactional
    public Result<String> likeHospital(String mgtNo, String userId) {
        // 중복 좋아요 체크
        Optional<HospitalLike> existingLike = hospitalLikeRepository.findByMgtNoAndUserId(mgtNo, userId);
        if (existingLike.isPresent()) {
            throw new IllegalArgumentException("이미 좋아요를 누르셨습니다.");
        }

        // 좋아요 추가
        HospitalLike newLike = new HospitalLike();
        newLike.setMgtNo(mgtNo);
        newLike.setUserId(userId);
        hospitalLikeRepository.save(newLike);

        // 병원 좋아요 수 증가
        AnimalHospital hospital = animalHospitalRepository.findById(mgtNo)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));
        hospital.setLikeCount(hospital.getLikeCount() + 1);
        animalHospitalRepository.save(hospital);

        return new Result<>(ResultCode.SUCCESS, "좋아요가 성공적으로 추가되었습니다.");
    }

    @Transactional
    public Result<String> unlikeHospital(String mgtNo, String userId) {
        // 좋아요 기록 조회
        HospitalLike existingLike = hospitalLikeRepository.findByMgtNoAndUserId(mgtNo, userId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요를 누른 적이 없습니다."));

        // 좋아요 기록 삭제
        hospitalLikeRepository.delete(existingLike);

        // 병원 좋아요 수 감소
        AnimalHospital hospital = animalHospitalRepository.findById(mgtNo)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));
        hospital.setLikeCount(Math.max(hospital.getLikeCount() - 1, 0)); // 최소값 0으로 설정
        animalHospitalRepository.save(hospital);

        return new Result<>(ResultCode.SUCCESS, "좋아요가 성공적으로 취소되었습니다.");
    }

    @Transactional(readOnly = true)
    public long getLikeCount(String mgtNo) {
        AnimalHospital hospital = animalHospitalRepository.findById(mgtNo)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));
        return hospital.getLikeCount();
    }

    //동물병원 상세정보
    @Transactional(readOnly = true)
    public AnimalHospitalDetailsDto getHospitalDetailsDto(String mgtNo) {
        AnimalHospital hospital = animalHospitalRepository.findById(mgtNo)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));

        // DTO로 변환
        return new AnimalHospitalDetailsDto(
                hospital.getBplcNm(),
                hospital.getSiteTel(),
                hospital.getSiteWhlAddr(),
                hospital.getRdnWhlAddr(),
                extractZipCode(hospital.getRdnWhlAddr()) // 주소에서 우편번호 추출 로직
        );
    }

    // 주소에서 우편번호 추출 (예시: 우편번호가 주소에 포함되어 있을 경우)
    private String extractZipCode(String rdnWhlAddr) {
        if (rdnWhlAddr == null || !rdnWhlAddr.matches(".*\\d{5}.*")) {
            return "우편번호 없음";
        }
        return rdnWhlAddr.replaceAll(".*(\\d{5}).*", "$1");
    }
}
