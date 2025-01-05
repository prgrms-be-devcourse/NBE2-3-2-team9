package com.team9.anicare.domain.schedule.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingleScheduleDTO {
    private Long id;
    private Long petId;
    private String petName;
    private Long userId;
    private String name;
    private Long periodicScheduleId;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @ToString
    public static class AddSingleScheduleDTO {
        private Long petId;
        private String name;
        private LocalDateTime startDatetime;
        private LocalDateTime endDatetime;
        private String petName;
    }

    @Getter
    @Setter
    public static class UpdateSingleScheduleDTO {
        private Long id;
        private Long petId;
        private String name;
        private LocalDateTime startDatetime;
        private LocalDateTime endDatetime;
        private String petName;
    }
}
