package com.yyl.base.feign;

import feign.*;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Date;

import static feign.FeignException.errorStatus;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description LeafFeignClient配置，因为此类不是spring的bean，所以作用于不是全局的，仅作用于configuration为此类的feignclient
 * @createTime 2022/11/21 17:10
 */
@Slf4j
public class LeafFeignClientConfigure {

    @Value("${feign.period:3000}")
    private long feignPeriod;

    @Value("${feign.maxPeriod:3000}")
    private long feignMaxPeriod;
    /**
     * 最大重试次数
     */
    @Value("${feign.maxAttempts:5}")
    private int feignMaxAttempts;

    /**
     * 重试类
     * @return
     */
    @Bean
    public Retryer feignRetryer(){
        return new Retryer.Default(feignPeriod, feignMaxPeriod, feignMaxAttempts);
    }

    /**
     * 自定义错误解码器
     * @return
     */
    @Bean
    public ErrorDecoder FeignErrorDecoder(){
        return (methodKey,response)->{
            FeignException exception = errorStatus(methodKey, response);
            //状态码为4xx不重试
            if(400<=response.status()&&response.status()<=499){
                return exception;
            }
            Request.HttpMethod httpMethod = response.request().httpMethod();
            //get请求重试
            if(httpMethod==Request.HttpMethod.GET){
                return new RetryableException(
                        response.status(),
                        exception.getMessage(),
                        response.request().httpMethod(),
                        exception,
                        new Date(),
                        response.request());
            }
            return exception;
        };
    }
}
