package com.team9.anicare.hospital.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team9.anicare.hospital.model.AnimalHospital;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 실제 서울시 공공데이터 JSON의 "row"마다 맵핑될 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class AnimalHospitalDto {

    @JsonProperty("OPNSFTEAMCODE")
    private String opnsfTeamCode;  // 개방자치단체코드

    @JsonProperty("MGTNO")
    private String mgtNo;          // 관리번호

    @JsonProperty("APVPERMYMD")
    private String apvPermYmd;     // 인허가일자

    @JsonProperty("TRDSTATEGBN")
    private String trdStateGbn;    // 영업상태코드

    @JsonProperty("TRDSTATENM")
    private String trdStateNm;     // 영업상태명

    @JsonProperty("SITETEL")
    private String siteTel;        // 전화번호

    @JsonProperty("SITEWHLADDR")
    private String siteWhlAddr;    // 지번주소

    @JsonProperty("RDNWHLADDR")
    private String rdnWhlAddr;     // 도로명주소

    @JsonProperty("BPLCNM")
    private String bplcNm;         // 사업장명

    @JsonProperty("UPTAENM")
    private String uptaeNm;        // 업태구분명

    // (나머지 필드 필요 시 추가)
    @JsonProperty("X")
    private String longitude;
    @JsonProperty("Y")
    private String latitude;

    /**
     * DTO → Entity 변환 메서드
     */
    public AnimalHospital toEntity() {
        AnimalHospital entity = new AnimalHospital();
        entity.setMgtNo(this.mgtNo);
        entity.setOpnsfTeamCode(this.opnsfTeamCode);
        entity.setApvPermYmd(this.apvPermYmd);
        entity.setTrdStateGbn(this.trdStateGbn);
        entity.setTrdStateNm(this.trdStateNm);
        entity.setSiteTel(this.siteTel);
        entity.setSiteWhlAddr(this.siteWhlAddr);
        entity.setRdnWhlAddr(this.rdnWhlAddr);
        entity.setBplcNm(this.bplcNm);
        entity.setUptaeNm(this.uptaeNm);
        entity.setLongitude(this.longitude);
        entity.setLatitude(this.latitude);
        return entity;
    }
}