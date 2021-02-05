package com.yyl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2020-12-09
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NacosProvider {
    public static   void main(String[] args) {
        SpringApplication.run(NacosProvider.class,args);
    }
}
