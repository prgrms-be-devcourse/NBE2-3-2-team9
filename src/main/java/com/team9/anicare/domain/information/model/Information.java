package com.team9.anicare.domain.information.model;

import com.team9.anicare.domain.animal.model.Breed;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Information {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "breed_id")
    private Breed breed;

    private String picture;
    private String age;
    private String weight;
    private String height;
    private String guide;
    private String description;
    private int hit;
}
