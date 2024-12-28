package com.team9.anicare.schedule.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Entity
@Table(name = "periodic_schedule")
@Setter
@Getter
@ToString
public class PeriodicSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_datetime", nullable = false)
    private Date startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private Date endDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", nullable = false)
    private RepeatType repeatType;

    @Column(name = "repeat_interval")
    private String repeatInterval;

    @Column(name = "weekdays")
    private String weekdays;

    @PrePersist
    private void setDefaultValue() {
        if(this.repeatInterval == null) {
            this.repeatInterval = "1";
        }
    }
}


