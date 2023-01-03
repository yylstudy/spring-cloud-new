package com.yyl.base.exception;


/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description 业务异常
 * @createTime 2022/3/16 14:14
 */

public class BusinessException extends RuntimeException{

    private int code = 500;

    public BusinessException(String message){
        super(message);
    }
    public BusinessException(int code, String message){
        super(message);
        this.code = code;
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message,cause);
    }
    public BusinessException(int code, String message, Throwable cause) {
        super(message,cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
