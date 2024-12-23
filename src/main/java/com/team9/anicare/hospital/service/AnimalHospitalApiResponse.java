package com.team9.anicare.hospital.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team9.anicare.hospital.dto.AnimalHospitalDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 최상위 루트 JSON 객체
 * {
 *   "LOCALDATA_020301_JN": {
 *       "list_total_count": 123,
 *       "RESULT": { "CODE": "...", "MESSAGE": "..." },
 *       "row": [
 *          {...},
 *          {...}
 *        ]
 *   }
 * }
 */
@Getter
@Setter
@NoArgsConstructor
public class AnimalHospitalApiResponse {

    // "LOCALDATA_020301_DJ" 이라는 키 값으로 내려옴 = 동작구
    @JsonProperty("LOCALDATA_020301_DJ")
    private Localdata020301Dj localdata020301Dj;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Localdata020301Dj {
        @JsonProperty("list_total_count")
        private int listTotalCount;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("row")
        private List<AnimalHospitalDto> row;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Result {
        @JsonProperty("CODE")
        private String code;

        @JsonProperty("MESSAGE")
        private String message;
    }
}
