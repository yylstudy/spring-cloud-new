package com.yyl.base;

import com.alibaba.cloud.nacos.NacosConfigAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/15 16:28
 */
@ComponentScan("com.yyl.base")
@EnableFeignClients(basePackages = "com.yyl.base.feign")
@AutoConfigureAfter(NacosConfigAutoConfiguration.class)
public class CommonAutoConfig {
}
