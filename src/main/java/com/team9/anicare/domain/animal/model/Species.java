package com.team9.anicare.domain.animal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "species")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Species {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;
}
