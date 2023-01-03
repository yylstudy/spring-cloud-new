//package com.yyl.gateway.config;
//
//import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import reactor.core.publisher.Mono;
//
///**
// * @author yang.yonglian
// * @version 1.0.0
// * @Description TODO
// * @createTime 2023/1/2 13:14
// */
//@Configuration
//public class RateLimiterConfiguration {
//    /**
//     * IP限流 (通过exchange对象可以获取到请求信息，这边用了HostName)
//     */
//    @Bean
//    @Primary
//    public KeyResolver ipKeyResolver() {
//        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
//    }
//}
