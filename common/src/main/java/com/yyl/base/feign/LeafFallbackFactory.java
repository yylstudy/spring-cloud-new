package com.yyl.base.feign;

import com.yyl.base.exception.BusinessException;
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
public class LeafFallbackFactory implements FallbackFactory<LeafFeignClient> {
    @Override
    public LeafFeignClient create(Throwable cause) {
        return new LeafFeignClient(){
            @Override
            public long getSnowflakeId() {
                log.error("获取分布式id失败",cause);
                throw new BusinessException("获取分布式id失败");
            }
        };
    }
}
