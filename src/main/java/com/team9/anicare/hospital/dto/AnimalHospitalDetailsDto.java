package com.team9.anicare.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnimalHospitalDetailsDto {
    private String bplcNm; // 병원 이름
    private String siteTel; // 병원 전화번호
    private String siteWhlAddr; // 병원 전체 주소
    private String rdnWhlAddr; // 병원 도로명 주소
    private String zipCode; // 우편번호 (필요 시 추가)
}