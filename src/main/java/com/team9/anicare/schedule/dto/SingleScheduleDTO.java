package com.team9.anicare.schedule.dto;


import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingleScheduleDTO {
    private Long id;
    private Long petId;
    private Long userId;
    private String name;
    private Date startDatetime;
    private Date endDatetime;

    @Getter
    @Setter
    @ToString
    public static class addSingleScheduleDTO {
        private Long petId;
        private String name;
        private Date startDatetime;
        private Date endDatetime;
    }

    @Getter
    @Setter
    public static class updateSingleScheduleDTO {
        private Long id;
        private Long petId;
        private String name;
        private Date startDatetime;
        private Date endDatetime;
    }
}
