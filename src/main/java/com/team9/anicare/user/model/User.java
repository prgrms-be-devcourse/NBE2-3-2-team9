package com.team9.anicare.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team9.anicare.common.entities.CommonEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

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
    @JsonIgnore
    private String password;

    @Column()
    private String profileImg;


    @Column(nullable = false)
    private int years_of_experience = 0;
    @Column()
    @JsonIgnore
    private String refreshtoken;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;


    @PrePersist
    public void prePersist() {
        if (this.profileImg == null || this.profileImg.isEmpty()) {
            this.profileImg = generateGravatarUrl(this.email);
        }
    }


    private String generateGravatarUrl(String email) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(email.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return "https://www.gravatar.com/avatar/" + hexString + "?s=200&r=pg&d=mm";
        } catch (Exception e) {
            throw new RuntimeException("Error generating Gravatar URL", e);
        }
    }

}
