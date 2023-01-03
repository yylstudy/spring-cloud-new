package com.yyl.base.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/21 16:46
 */
@FeignClient(value = "leaf-server",contextId = "leaf-server",fallbackFactory = LeafFallbackFactory.class,configuration = LeafFeignClientConfigure.class)
public interface LeafFeignClient {
    /**
     * 获取雪花ID
     * @return
     */
    @GetMapping(value = "/api/snowflake/get/1")
    long getSnowflakeId();
}
