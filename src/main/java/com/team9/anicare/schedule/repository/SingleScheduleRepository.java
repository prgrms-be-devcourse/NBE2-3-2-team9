package com.team9.anicare.schedule.repository;


import com.team9.anicare.schedule.model.SingleSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SingleScheduleRepository extends JpaRepository<SingleSchedule,Long> {
    @Query("select s from SingleSchedule s where s.userId = :userId")
    List<SingleSchedule> findSingleSchedulesByUserId(Long userId);

    //@Query("select p.id from Pet p where p.userId = :userId and p.id = :petId")
    //Long findPetIdByUserIdAndPetId(Long userId, Long petId);
}
