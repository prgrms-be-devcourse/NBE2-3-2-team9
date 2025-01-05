package com.team9.anicare.domain.hospital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "animal_hospital")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalHospital {

    @Id
    private String mgtNo;

    private String opnsfTeamCode;

    private String apvPermYmd;

    private String trdStateGbn;

    private String trdStateNm;

    private String siteTel;

    private String siteWhlAddr;

    private String rdnWhlAddr;

    private String bplcNm;

    private String uptaeNm;

    private String xCode;

    private String yCode;

    private double latitude;

    private double longitude;

    @Column(nullable = false)
    private int likeCount; // 좋아요 기본값 0
}
