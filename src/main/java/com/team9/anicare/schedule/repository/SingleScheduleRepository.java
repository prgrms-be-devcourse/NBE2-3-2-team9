package com.team9.anicare.schedule.repository;


import com.team9.anicare.schedule.model.SingleSchedule;
import com.team9.anicare.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SingleScheduleRepository extends JpaRepository<SingleSchedule,Long> {
   @Query("select s from SingleSchedule s where s.user = :user")
   List<SingleSchedule> findSingleSchedulesByUser(User user);
}
