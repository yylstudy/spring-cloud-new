package com.yyl.gateway.config;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description 自定义全局异常处理器，主要是重写getHttpStatus，因为这边定义的json返回没有status字段
 *              所以使用DefaultErrorWebExceptionHandler时会报错，这个是要需要使用GlobalErrorAttributes中已经定义的字段
 *              这边是code
 * @createTime 2023/1/2 14:59
 */

public class GlobalErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
                                           ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resources,errorProperties, applicationContext);
    }

    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        return (int) errorAttributes.get("code");
    }
}
