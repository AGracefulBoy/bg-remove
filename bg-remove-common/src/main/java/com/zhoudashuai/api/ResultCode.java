package com.zhoudashuai.api;

public enum ResultCode implements IErrorCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "token失效,请重试"),
    PAYMENT_REQUIRED(402, "账户余额不足,请付费"),
    FORBIDDEN(403, "没有相关权限"),

    UNKNOWN_ERROR(99999, "未知错误"),
    ;
    private final long code;
    private final String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
