package com.team9.anicare.pet.model;

import com.team9.anicare.common.entities.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

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

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "species_id")
    private Long speciesId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age")
    private String age;

    @Column(name = "picture")
    private String picture;

    @Column(name = "gender")
    private String  gender;
}
