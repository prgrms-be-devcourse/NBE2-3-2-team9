package com.team9.anicare.common.exception;

import com.team9.anicare.common.ResultCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException{
    private final ResultCode resultCode;

    public CustomException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.resultCode = resultCode;
    }
}
