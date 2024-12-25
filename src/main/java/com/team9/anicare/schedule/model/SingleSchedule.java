package com.team9.anicare.schedule.model;


import com.team9.anicare.common.entities.CommonEntity;
import com.team9.anicare.pet.model.Pet;
import com.team9.anicare.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;


@Entity
@Table(name = "single_schedule")
@Getter
@Setter
@ToString
public class SingleSchedule extends CommonEntity {
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

    @ManyToOne
    @JoinColumn(name = "periodic_schedule_id")
    private PeriodicSchedule periodicSchedule;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_datetime", nullable = false)
    private Date startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private Date endDatetime;

}


