package com.team9.anicare.domain.schedule.repository;

import com.team9.anicare.domain.schedule.model.PeriodicSchedule;
import com.team9.anicare.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeriodicScheduleRepository extends JpaRepository<PeriodicSchedule, Long> {
    @Query("select ps from PeriodicSchedule ps where ps.user = :user")
    List<PeriodicSchedule> findPeriodicSchedulesByUserId(User user);
}
