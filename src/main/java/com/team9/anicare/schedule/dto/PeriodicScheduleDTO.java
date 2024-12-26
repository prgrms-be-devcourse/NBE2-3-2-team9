package com.team9.anicare.schedule.dto;


import com.team9.anicare.schedule.model.RepeatPattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodicScheduleDTO {
    private Long id;
    private Long userId;
    private Long petId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private RepeatPattern repeatPattern;
    private int repeatInterval;
    private String repeatDays;

    @Getter
    @Setter
    public static class addPeriodicScheduleDTO {
        private Long petId;
        private String name;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private RepeatPattern repeatPattern;
        private int repeatInterval;
        private String repeatDays;
    }

    @Getter
    @Setter
    public static class updatePeriodicScheduleDTO {
        private Long id;
        private Long petId;
        private String name;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private RepeatPattern repeatPattern;
        private int repeatInterval;
        private String repeatDays;
    }
}
