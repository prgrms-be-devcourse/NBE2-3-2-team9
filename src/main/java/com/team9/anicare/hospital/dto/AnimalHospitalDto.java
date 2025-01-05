package com.team9.anicare.hospital.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team9.anicare.hospital.model.AnimalHospital;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AnimalHospitalDto {


    private String opnsfTeamCode;


    private String mgtNo;


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

    public  AnimalHospital toEntity() {
        return AnimalHospital.builder()
                .opnsfTeamCode(this.opnsfTeamCode)
                .mgtNo(this.mgtNo)
                .apvPermYmd(this.apvPermYmd)
                .trdStateGbn(this.trdStateGbn)
                .trdStateNm(this.trdStateNm)
                .siteTel(this.siteTel)
                .siteWhlAddr(this.siteWhlAddr)
                .rdnWhlAddr(this.rdnWhlAddr)
                .bplcNm(this.bplcNm)
                .uptaeNm(this.uptaeNm)
                .xCode(this.xCode)
                .yCode(this.yCode)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .build();
    }
}
