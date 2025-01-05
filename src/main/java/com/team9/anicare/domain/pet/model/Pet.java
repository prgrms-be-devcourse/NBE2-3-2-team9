package com.team9.anicare.domain.pet.model;

import com.team9.anicare.domain.animal.model.Breed;
import com.team9.anicare.domain.animal.model.Species;
import com.team9.anicare.common.entities.CommonEntity;
import com.team9.anicare.domain.schedule.model.PeriodicSchedule;
import com.team9.anicare.domain.schedule.model.SingleSchedule;
import com.team9.anicare.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "pet")
@Getter
@Setter
@ToString
public class Pet extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    @ManyToOne
    @JoinColumn(name = "breed_id", nullable = false)
    private Breed breed;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age")
    private String age;

    @Column(name = "picture")
    private String picture;

    @Column(name = "gender")
    private String gender;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.REMOVE)
    private List<SingleSchedule> singleSchedules;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.REMOVE)
    private List<PeriodicSchedule> periodicSchedule;
}
