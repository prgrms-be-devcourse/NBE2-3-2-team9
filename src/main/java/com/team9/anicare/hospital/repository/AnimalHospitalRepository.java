package com.team9.anicare.hospital.repository;

import com.team9.anicare.hospital.model.AnimalHospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for AnimalHospital Entity
 */
@Repository
public interface AnimalHospitalRepository extends JpaRepository<AnimalHospital, String> {
    // 필요 시 추가 Query 메서드 작성
}