package com.team9.anicare.schedule.model;


import com.team9.anicare.common.entities.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;


@Entity
@Table(name = "schedule")
@Getter
@Setter
public class Schedule extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "pet_id")
    private Long petId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "description")
    private String description;

    @Column(name = "schedule_start")
    private Date scheduleStart;

    @Column(name = "schedule_end")
    private Date scheduleEnd;
}
