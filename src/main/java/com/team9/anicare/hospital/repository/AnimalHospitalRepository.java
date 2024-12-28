package com.team9.anicare.hospital.repository;


import com.team9.anicare.hospital.model.AnimalHospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalHospitalRepository extends JpaRepository<AnimalHospital, String> {
    //동물병원 검색
    List<AnimalHospital> findByBplcNmContaining(String hospitalName);

}
