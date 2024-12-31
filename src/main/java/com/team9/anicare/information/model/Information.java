package com.team9.anicare.information.model;

import com.team9.anicare.species.model.Breed;
import com.team9.anicare.species.model.Species;
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
