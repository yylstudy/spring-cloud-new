package com.yyl.demo;

import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreaker;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.EventObserverRegistry;
import com.alibaba.csp.sentinel.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/12/7 16:47
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@Slf4j
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class,args);
        EventObserverRegistry.getInstance().addStateChangeObserver("logging",
                (prevState, newState, rule, snapshotValue) -> {
                    String resource = rule.getResource();
                    log.info("资源：{}，熔断器状态发生变更，从{}变成{},snapshotValue:{}",
                            resource,prevState,newState,snapshotValue);
                });

    }

}
