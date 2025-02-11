package com.team9.anicare.domain.schedule.dto;


import com.team9.anicare.domain.schedule.model.RepeatPattern;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodicScheduleDTO {
    private Long id;
    private Long userId;
    private Long petId;
    private String petName;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private RepeatPattern repeatPattern;
    private int repeatInterval;
    private String repeatDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    public static class AddPeriodicScheduleDTO {
        private Long petId;
        private String petName;
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
    public static class UpdatePeriodicScheduleDTO {
        private Long id;
        private Long petId;
        private String petName;
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
