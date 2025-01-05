package com.team9.anicare.hospital.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hospital_like", uniqueConstraints = {
        @UniqueConstraint(name = "unique_hospital_user", columnNames = {"mgt_no", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HospitalLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mgt_no", nullable = false)
    private String mgtNo;

    @Column(name = "user_id", nullable = false)
    private String userId;
}
