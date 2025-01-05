package com.team9.anicare.domain.schedule.model;


import com.team9.anicare.common.entities.CommonEntity;
import com.team9.anicare.domain.pet.model.Pet;
import com.team9.anicare.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "periodic_schedule")
@Setter
@Getter
@ToString
public class PeriodicSchedule extends CommonEntity {
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

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_pattern", nullable = false)
    private RepeatPattern repeatPattern;

    @Column(name = "repeat_interval", nullable = false)
    private int repeatInterval;

    @Column(name = "repeat_days")
    private String repeatDays;

    @OneToMany(mappedBy = "periodicSchedule", cascade = CascadeType.REMOVE)
    private List<SingleSchedule> singleSchedules;
}


