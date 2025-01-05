package com.team9.anicare.hospital.repository;

import com.team9.anicare.hospital.model.AnimalHospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalHospitalRepository extends JpaRepository<AnimalHospital, String> {
    // 동물 병원 이름 검색
    List<AnimalHospital> findByBplcNmContaining(String hospitalName);

    // 도로명 주소 검색 (rdn_whl_addr)
    List<AnimalHospital> findByRdnWhlAddrContaining(String rdnWhlAddr);

    // 일반 주소 검색 (site_whl_addr)
    List<AnimalHospital> findBySiteWhlAddrContaining(String siteWhlAddr);

    //단어 검색
    List<AnimalHospital> findByRdnWhlAddrContainingOrSiteWhlAddrContaining(String rdnWhlAddrKeyword, String siteWhlAddrKeyword);

    List<AnimalHospital> findByRdnWhlAddrContainingAndSiteWhlAddrContaining(String rdnWhlAddr, String siteWhlAddr);

}
