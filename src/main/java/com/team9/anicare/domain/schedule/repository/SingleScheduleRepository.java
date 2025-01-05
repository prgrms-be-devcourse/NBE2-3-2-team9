package com.team9.anicare.domain.schedule.repository;


import com.team9.anicare.domain.schedule.model.PeriodicSchedule;
import com.team9.anicare.domain.schedule.model.SingleSchedule;
import com.team9.anicare.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SingleScheduleRepository extends JpaRepository<SingleSchedule,Long> {
   @Query("select s from SingleSchedule s where s.user = :user")
   List<SingleSchedule> findSingleSchedulesByUser(User user);

   @Modifying
   @Query("delete SingleSchedule s where s.periodicSchedule = :periodicSchedule")
   void deleteByPeriodicSchedule(PeriodicSchedule periodicSchedule);

   @Query("select s.periodicSchedule from SingleSchedule s where s.id = :singleScheduleId")
   PeriodicSchedule findPeriodicScheduleById(Long singleScheduleId);

   @Query("select count(s) from SingleSchedule s where s.periodicSchedule = :periodicSchedule")
   Long countByPeriodicScheduleId(PeriodicSchedule periodicSchedule);

}
