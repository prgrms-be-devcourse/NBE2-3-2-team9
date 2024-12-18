package com.team9.anicare.schedule.repository;

import com.team9.anicare.schedule.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    @Query("select s from Schedule s where s.userId = :userId")
    List<Schedule> findSchedulesByUserId(Long userId);

    @Query("select p.id from Pet p where p.userId = :userId and p.id = :petId")
    Long findPetIdByUserIdAndPetId(Long userId, Long petId);
}
