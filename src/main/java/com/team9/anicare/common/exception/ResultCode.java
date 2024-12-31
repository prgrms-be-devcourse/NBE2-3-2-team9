package com.team9.anicare.common.exception;

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
    UNAUTHORIZED(401, "로그인이 필요합니다."),
    FORBIDDEN(403, "권한이 없습니다."),
    TOKEN_EXPIRED(401, "토큰이 만료되었습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    ACCESS_DENIED_ADMIN(403, "해당 리소스에 관리자 권한이 필요합니다."),
    ACCESS_DENIED_USER(403, "해당 리소스에 사용자 권한이 필요합니다."),


    // 유저 관련
    NOT_EXISTS_USER(404, "해당 유저가 존재하지 않습니다."),
    FAIL_TO_SAVE_USER(500, "유저 정보 저장에 실패했습니다."),
    EMAIL_ALREADY_EXISTS(409, "이미 등록된 이메일입니다."),
    INVALID_USER_PASSWORD(400, "유효하지 않은 비밀번호입니다."),
    USER_ACCOUNT_LOCKED(403, "사용자 계정이 잠겼습니다."),

    NOT_FOUND(404, "권한이 없습니다."),


    // 반려동물 관련
    NOT_EXISTS_PET(404, "반려동물을 등록하지 않았습니다."),
    INVALID_GENDER_VALUE(400,"올바르지 않은 성별입니다"),

    // 종 관련
    NOT_EXISTS_SPECIES(404, "해당하는 종이 없습니다."),
    NOT_EXISTS_BREED(404, "해당하는 품종이 없습니다"),
    DUPLICATE_SPECIES(409, "이미 존재하는 종입니다"),
    DUPLICATE_SPECIES_AND_BREED(409, "이미 존재하는 종과 품종입니다"),
    NOT_EXISTS_BREED(404, "종에 해당되는 품종이 등록되어 있지 않습니다"),

    // 동물 정보 관련
    NOT_EXISTS_INFORMATION(404, "해당하는 정보가 없습니다"),
    DUPLICATE_INFORMATION(409, "이미 작성한 정보입니다"),

    // 스케줄 관련
    NOT_EXISTS_SCHEDULE(404, "요청하신 스케줄이 없습니다."),
    INVALID_DATETIME_VALUE(400,"올바르지 않은 날짜 입력입니다"),

    // 커뮤니티 관련
    NOT_EXISTS_POST(404, "게시글이 존재하지 않습니다."),
    NOT_EXISTS_COMMENT(404, "댓글이 존재하지 않습니다"),
    DUPLICATE_LIKE(409, "이미 좋아요를 누른 상태입니다"),

    // 파일 관련
    EMPTY_FILE_NAME(400, "파일 이름이 비었습니다"),
    INVALID_FILE_EXTENSION(415, "알맞지 않은 파일 형식입니다"),
    FILE_UPLOAD_ERROR(500, "파일 업로드를 실패했습니다"),

    // 500 Internal Server Error
    DB_ERROR(500, "DB오류 입니다."),

    ETC_ERROR(500, "알 수 없는 이유로 실패했습니다.");

    private int code;
    private String msg;
}
