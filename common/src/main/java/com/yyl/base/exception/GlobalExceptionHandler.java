package com.yyl.base.exception;

import com.yyl.base.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/3/16 14:14
 */
@RestControllerAdvice
@Order(-1)
@Slf4j
public class GlobalExceptionHandler {
    @Value("{defaultExceptionMessage:服务异常，请重试}")
    private String defaultExceptionMessage;


    @ExceptionHandler(CustomException.class)
    public Result handlerCustomException(CustomException customException){
        String message = customException.getMessage();
        if(!StringUtils.hasText(message)){
            message = "自定义异常";
        }
        return Result.error(customException.getCode(),message);
    }


    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleArgumentNotValidException(Exception e){
        BindingResult bindingResult;
        if(e instanceof MethodArgumentNotValidException){
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        }else{
            bindingResult = ((BindException)e).getBindingResult();
        }
        String errorMsg = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(","));
        Result result = Result.error(errorMsg);
        return result;
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handlerNoFoundException(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return Result.error(404,"请求路径:"+e.getRequestURL()+"不存在，请检查路径是否正确");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        StringBuffer sb = new StringBuffer();
        sb.append("不支持");
        sb.append(e.getMethod());
        sb.append("请求方法，");
        sb.append("支持以下");
        String [] methods = e.getSupportedMethods();
        if(methods!=null){
            for(String str:methods){
                sb.append(str);
                sb.append("、");
            }
        }
        log.error(sb.toString(), e);
        return Result.error(405,sb.toString());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handlerBusinessException(BusinessException businessException){
        String message = businessException.getMessage();
        if(!StringUtils.hasText(message)){
            message = "业务异常，请重试";
        }
        return Result.error(businessException.getCode(),message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handlerException(Exception e, HandlerMethod handlerMethod){
        log.error("call server error",e);
        String message = e.getMessage();
        if(!StringUtils.hasText(message)){
            message = defaultExceptionMessage;
        }
        return Result.error(message);
    }

}
