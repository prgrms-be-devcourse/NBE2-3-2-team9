package com.team9.anicare.domain.animal.repository;

import com.team9.anicare.domain.animal.model.Breed;
import com.team9.anicare.domain.animal.model.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreedRepository extends JpaRepository<Breed,Long> {

    boolean existsByNameAndSpecies(String name, Species species);

    List<Breed> findBreedsBySpecies(Species species);

    Breed findByName(String name);

}
