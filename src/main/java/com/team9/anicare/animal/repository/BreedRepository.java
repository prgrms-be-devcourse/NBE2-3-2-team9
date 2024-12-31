package com.team9.anicare.animal.repository;

import com.team9.anicare.animal.model.Breed;
import com.team9.anicare.animal.model.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreedRepository extends JpaRepository<Breed,Long> {

    boolean existsByNameAndSpecies(String name, Species species);
}
