package com.yyl.gateway.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.stream.Collectors;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description gateway配置类
 * @createTime 2022/11/28 17:34
 */
@Configuration
public class GatewayConfig {
    /**
     * 用于在gateway上远程调用，在Spring Cloud 2020版本后，spring cloud gateway集成openfeign会卡住无法启动
     * 详细查看https://github.com/spring-cloud/spring-cloud-gateway/issues/2126
     * 解决方法官方给出的通过WebClient去调用
     * @return
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder webBuilder(){
        return WebClient.builder();
    }

    @Bean
    @Order(-1)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ServerProperties serverProperties,ErrorAttributes errorAttributes,
                                                             WebProperties webProperties, ObjectProvider<ViewResolver> viewResolvers,
                                                             ServerCodecConfigurer serverCodecConfigurer, ApplicationContext applicationContext) {
        GlobalErrorWebExceptionHandler exceptionHandler = new GlobalErrorWebExceptionHandler(errorAttributes,
                webProperties.getResources(), serverProperties.getError(), applicationContext);
        exceptionHandler.setViewResolvers(viewResolvers.orderedStream().collect(Collectors.toList()));
        exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }
    @Bean
    public GlobalErrorAttributes globalErrorAttributes(){
        return new GlobalErrorAttributes();
    }


}
