package com.team9.anicare.schedule.repository;

import com.team9.anicare.schedule.model.PeriodicSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeriodicScheduleRepository extends JpaRepository<PeriodicSchedule, Long> {
    @Query("select ps from PeriodicSchedule ps where ps.userId = :userId")
    List<PeriodicSchedule> findPeriodicSchedulesByUserId(Long userId);
}
