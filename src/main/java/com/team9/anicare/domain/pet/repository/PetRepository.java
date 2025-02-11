package com.team9.anicare.domain.pet.repository;

import com.team9.anicare.domain.pet.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet,Long> {
    List<Pet> findAllByUserId(Long userId);
}
