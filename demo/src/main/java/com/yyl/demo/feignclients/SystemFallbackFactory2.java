package com.yyl.demo.feignclients;

import com.yyl.demo.dto.UserRepDTO;
import com.yyl.demo.dto.UserReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/21 16:46
 */
@Slf4j
@Component
public class SystemFallbackFactory2 implements FallbackFactory<SystemFeignClient2> {
    @Override
    public SystemFeignClient2 create(Throwable cause) {
        return new SystemFeignClient2(){
            @Override
            public UserRepDTO testDelegate(String str) {
                log.error("SystemFeignClient2 testDelegate error");
                return null;
            }
        };
    }
}
