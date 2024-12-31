package com.team9.anicare.species.repository;

import com.team9.anicare.species.model.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SpeciesRepository extends JpaRepository<Species,Long> {

    Species findByName(String name);

}
