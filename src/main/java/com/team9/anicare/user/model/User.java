package com.team9.anicare.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String profileImg;
    private String name;
    private String password;
    private String role;
    private int years_of_experience;
    private String refreshtoken;
}
