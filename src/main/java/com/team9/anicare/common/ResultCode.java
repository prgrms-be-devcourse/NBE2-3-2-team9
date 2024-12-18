package com.team9.anicare.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    //성공
    SUCCESS(200,"성공"),
    // 요청 검증 실패
    INVALID_REQUEST(400, "입력값 검증에 실패했습니다."),

    UNSUPPORTED_HTTP_METHOD(405, "지원하지 않는 HTTP 메서드입니다."),

    MISSING_PARAMETER(400, "필수 요청 파라미터가 누락되었습니다."),
    // JSON 파싱 오류
    INVALID_JSON(400, "잘못된 JSON 형식입니다."),

    // 유저 관련
    NOT_EXISTS_USER(404, "해당 유저가 존재하지 않습니다."),
    FAIL_TO_SAVE_USER(500, "유저 정보 저장에 실패했습니다."),
    EMAIL_ALREADY_EXISTS(409, "이미 등록된 이메일입니다."),
    INVALID_USER_PASSWORD(400, "유효하지 않은 비밀번호입니다."),
    USER_ACCOUNT_LOCKED(403, "사용자 계정이 잠겼습니다."),

    // 반려동물 관련
    NOT_EXISTS_PET(404, "반려동물을 등록하지 않았습니다."),


    // 스케줄 관련
    NOT_EXISTS_SCHEDULE(404, "요청하신 스케줄이 없습니다."),

    // 500 Internal Server Error
    DB_ERROR(500, "DB오류 입니다."),

    ETC_ERROR(500, "알 수 없는 이유로 실패했습니다.");

    private int code;
    private String msg;
}
