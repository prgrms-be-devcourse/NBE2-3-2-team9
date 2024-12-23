package com.team9.anicare.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ResultCode resultCode;

    public CustomException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.resultCode = resultCode;
    }
}
