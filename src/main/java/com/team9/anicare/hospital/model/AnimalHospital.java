package com.team9.anicare.hospital.model;

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

    private String latitude;

    private String longitude;
}
