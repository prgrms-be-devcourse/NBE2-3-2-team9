package com.team9.anicare.user.model;

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
public class User {
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

    @Column(updatable = false) // 생성 시에만 값 설정
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;


    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.profileImg == null || this.profileImg.isEmpty()) {
            this.profileImg = generateGravatarUrl(this.email);
        }
    }

    @PreUpdate // 엔티티 업데이트 전에 실행
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private String generateGravatarUrl(String email) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(email.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            // 기본 이미지 스타일: mm
            return "https://www.gravatar.com/avatar/" + hexString + "?s=200&r=pg&d=mm";
        } catch (Exception e) {
            throw new RuntimeException("Error generating Gravatar URL", e);
        }
    }

}
