package com.team9.anicare.schedule.model;


import com.team9.anicare.common.entities.CommonEntity;
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

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_datetime", nullable = false)
    private Date startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private Date endDatetime;
}
