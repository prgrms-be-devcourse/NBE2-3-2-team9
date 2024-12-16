package com.team9.anicare.common;

import lombok.Getter;

import java.time.Instant;

@Getter
public class Result<T> {

    private final int statusCode;           // 상태 코드
    private final String message;         // 메시지
    private final String timestamp;   // 타임스탬프
    private final T data;

    public Result(ResultCode resultCode) {
        this.statusCode = resultCode.getCode();
        this.message = resultCode.getMsg();
        this.timestamp = Instant.now().toString();
        this.data = null;
    }

    public Result(ResultCode resultCode, T resultObject) {
        this.statusCode = resultCode.getCode();
        this.message = resultCode.getMsg();
        this.timestamp = Instant.now().toString();
        this.data = resultObject;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + statusCode +
                ", msg='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", data=" + data +
                '}';
    }

}
