package com.team9.anicare.schedule.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Long id;
    private Long petId;
    private Long userId;
    private String description;
    private Date scheduleStart;
    private Date scheduleEnd;

    @Getter
    @Setter
    public static class addScheduleDTO {
        private Long petId;
        private String description;
        private Date scheduleStart;
        private Date scheduleEnd;
    }

    @Getter
    @Setter
    public static class updateScheduleDTO {
        private Long id;
        private Long petId;
        private String description;
        private Date scheduleStart;
        private Date scheduleEnd;
    }


}
