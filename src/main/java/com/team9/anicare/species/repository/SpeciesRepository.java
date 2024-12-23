package com.team9.anicare.species.repository;

import com.team9.anicare.schedule.model.SingleSchedule;
import com.team9.anicare.species.model.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeciesRepository extends JpaRepository<Species,Long> {
}
