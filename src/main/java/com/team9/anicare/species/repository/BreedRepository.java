package com.team9.anicare.species.repository;

import com.team9.anicare.species.model.Breed;
import com.team9.anicare.species.model.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreedRepository extends JpaRepository<Breed,Long> {

    boolean existsByNameAndSpecies(String name, Species species);

    Breed findByName(String name);

}
