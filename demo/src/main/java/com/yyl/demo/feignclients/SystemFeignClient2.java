package com.yyl.demo.feignclients;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.yyl.demo.dto.UserRepDTO;
import com.yyl.demo.dto.UserReqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/21 16:46
 */
@FeignClient(value = "system",contextId = "system2",fallbackFactory = SystemFallbackFactory2.class)
public interface SystemFeignClient2 {

    @PostMapping(value = "sys/user/testDelegate")
    UserRepDTO testDelegate( @RequestParam("str") String str);

}
