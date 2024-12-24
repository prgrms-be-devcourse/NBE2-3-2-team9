package com.team9.anicare.hospital.repository;


import com.team9.anicare.hospital.model.AnimalHospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalHospitalRepository extends JpaRepository<AnimalHospital, String> {
}