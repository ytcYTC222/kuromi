package com.mall.admin.common.exception;

/**
 * 认证异常类
 * 用于处理JWT认证相关的异常情况
 */
public class AuthException extends RuntimeException {

    private Integer code;

    public AuthException(String message) {
        super(message);
        this.code = 401;
    }

    public AuthException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
        this.code = 401;
    }

    public AuthException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
