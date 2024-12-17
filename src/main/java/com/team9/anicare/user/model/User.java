package com.team9.anicare.user.model;

import com.team9.anicare.common.entities.CommonEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( unique = true)
    private String email;

    @Column()
    private String name;

    @Column()
    private String password;

    @Column()
    private String profileImg;


    @Column(nullable = false)
    private int years_of_experience = 0;
    @Column()
    private String refreshtoken;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    public void setRole(Role role) {
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
