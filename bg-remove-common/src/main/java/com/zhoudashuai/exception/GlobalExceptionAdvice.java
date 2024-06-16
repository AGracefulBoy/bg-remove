package com.zhoudashuai.exception;

import com.zhoudashuai.api.CommonResult;
import com.zhoudashuai.api.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = ApiException.class)
    public CommonResult handle(ApiException e) {
        if (e.getErrorCode() != null) {
            return CommonResult.failed(e.getErrorCode());
        }
        return CommonResult.failed(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public CommonResult handle(Exception e) {
        log.error("error", e);
        return CommonResult.failed(ResultCode.UNKNOWN_ERROR, e.getMessage());
    }
}
