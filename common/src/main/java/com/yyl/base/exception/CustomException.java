package com.yyl.base.exception;


/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description 自定义异常
 * @createTime 2022/3/16 14:14
 */

public class CustomException extends RuntimeException{

    private int code = 500;

    public CustomException(String message){
        super(message);
    }
    public CustomException(int code, String message){
        super(message);
        this.code = code;
    }

    public CustomException(Throwable cause) {
        super(cause);
    }

    public CustomException(String message, Throwable cause) {
        super(message,cause);
    }
    public CustomException(int code, String message, Throwable cause) {
        super(message,cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
