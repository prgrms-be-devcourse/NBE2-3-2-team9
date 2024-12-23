package com.team9.anicare.hospital.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 서울시 종로구 동물병원 인허가 정보 Entity
 */
@Entity
@Table(name = "animal_hospital")
@Getter
@Setter
@NoArgsConstructor
public class AnimalHospital {

    /** 관리번호 (MGTNO)를 PK 로 가정 */
    @Id
    private String mgtNo;

    /** 개방자치단체코드 */
    private String opnsfTeamCode;

    /** 인허가일자 */
    private String apvPermYmd;

    /** 영업상태코드 */
    private String trdStateGbn;

    /** 영업상태명 */
    private String trdStateNm;

    /** 전화번호 */
    private String siteTel;

    /** 지번주소 */
    private String siteWhlAddr;

    /** 도로명주소 */
    private String rdnWhlAddr;

    /** 사업장명 */
    private String bplcNm;

    /** 업태구분명 */
    private String uptaeNm;

    /** 경도 */
    private String longitude;

    /** 위도 */
    private String latitude;


    // --- 필요시 추가 필드 ---
    // private String apvCancelYmd;  // 인허가취소일자
    // private String dtlStateGbn;   // 상세영업상태코드
    // private String dtlStateNm;    // 상세영업상태명
    // private String dcbyMd;        // 폐업일자
    // etc...

    // Lombok으로 getter/setter 처리
}
