package com.team9.anicare.hospital.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team9.anicare.hospital.model.AnimalHospital;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnimalHospitalDto {

    @JsonProperty("OPNSFTEAMCODE")
    private String opnsfTeamCode;

    @JsonProperty("MGTNO")
    private String mgtNo;

    @JsonProperty("APVPERMYMD")
    private String apvPermYmd;

    @JsonProperty("TRDSTATEGBN")
    private String trdStateGbn;

    @JsonProperty("TRDSTATENM")
    private String trdStateNm;

    @JsonProperty("SITETEL")
    private String siteTel;

    @JsonProperty("SITEWHLADDR")
    private String siteWhlAddr;

    @JsonProperty("RDNWHLADDR")
    private String rdnWhlAddr;

    @JsonProperty("BPLCNM")
    private String bplcNm;

    @JsonProperty("UPTAENM")
    private String uptaeNm;

    @JsonProperty("X")
    private String xCode;

    @JsonProperty("Y")
    private String yCode;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;

    public AnimalHospital toEntity() {
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
