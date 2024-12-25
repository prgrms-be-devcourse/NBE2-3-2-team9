package com.team9.anicare.schedule.model;


import com.team9.anicare.pet.model.Pet;
import com.team9.anicare.user.model.User;
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

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_datetime", nullable = false)
    private Date startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private Date endDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_pattern", nullable = false)
    private RepeatPattern repeatPattern;

    @Column(name = "repeat_interval")
    private String repeatInterval;

    @Column(name = "repeat_days")
    private String repeatDays;

    @PrePersist
    private void setDefaultValue() {
        if(this.repeatInterval == null) {
            this.repeatInterval = "1";
        }
    }
}


