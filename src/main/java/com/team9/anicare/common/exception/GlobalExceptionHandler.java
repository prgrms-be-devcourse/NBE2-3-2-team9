package com.team9.anicare.common.exception;


import com.team9.anicare.common.response.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<Result<Void>> handleCustomException(CustomException e) {
        // ResultCode에서 정보 가져오기
        Result<Void> result = new Result<>(e.getResultCode());

        return ResponseEntity
                .status(e.getResultCode().getCode())  // HTTP 상태 설정
                .body(result);                        // Result 반환
    }



    // JSON 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Result<Void> result = new Result<>(ResultCode.INVALID_JSON, null);
        return ResponseEntity.status(ResultCode.INVALID_REQUEST.getCode()).body(result);
    }

    // HTTP 메서드 오류
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        Result<Void> result = new Result<>(ResultCode.UNSUPPORTED_HTTP_METHOD, null);
        return ResponseEntity.status(ResultCode.UNSUPPORTED_HTTP_METHOD.getCode()).body(result);
    }


    // 요청 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Map<String, String>>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getParameterName(), "필수 파라미터가 누락되었습니다.");
        Result<Map<String, String>> result = new Result<>(ResultCode.MISSING_PARAMETER, errors);
        return ResponseEntity.status(ResultCode.MISSING_PARAMETER.getCode()).body(result);
    }

    // Validation 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        Result<Map<String, String>> result = new Result<>(ResultCode.INVALID_REQUEST, errors);
        return ResponseEntity.status(ResultCode.INVALID_REQUEST.getCode()).body(result);
    }

}
