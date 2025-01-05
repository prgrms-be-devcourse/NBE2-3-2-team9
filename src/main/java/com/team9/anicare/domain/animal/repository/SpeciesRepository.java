package com.team9.anicare.domain.animal.repository;

import com.team9.anicare.domain.animal.model.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SpeciesRepository extends JpaRepository<Species,Long> {

    Species findByName(String name);

}
