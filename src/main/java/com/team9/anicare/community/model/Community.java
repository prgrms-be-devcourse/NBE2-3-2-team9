package com.team9.anicare.community.model;

import com.team9.anicare.common.entities.CommonEntity;
import com.team9.anicare.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Community extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String content;

    private String picture;

    private String animalSpecies;

    private int commentCount;

    private int likeCount;
}
