package com.yyl.demo.feignclients;

import com.yyl.base.exception.BusinessException;
import com.yyl.demo.dto.UserRepDTO;
import com.yyl.demo.dto.UserReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/21 16:46
 */
@Slf4j
@Component
public class SystemFallbackFactory implements FallbackFactory<SystemFeignClient> {
    @Override
    public SystemFeignClient create(Throwable cause) {
        return new SystemFeignClient(){
            @Override
            public UserRepDTO add(UserReqDTO userReqDTO) {
                log.error("add error",cause);
                return null;
            }
            @Override
            public UserRepDTO addNormalExceptionHandler(UserReqDTO userReqDTO) {
                log.error("addNormalExceptionHandler error",cause);
                return null;
            }

            @Override
            public UserRepDTO testGetRetry(String name) {
                log.error("testRetry error",cause);
                return null;
            }

            @Override
            public UserRepDTO testPostRetry(UserReqDTO userDTO) {
                log.error("testPostRetry error",cause);
                return null;
            }
            @Override
            public UserRepDTO testGetException(String name) {
                log.error("testGetException error",cause);
                return null;
            }

            @Override
            public UserRepDTO testPostException(UserReqDTO userReqDTO) {
                log.error("testPostException error",cause);
                return null;
            }

            @Override
            public UserRepDTO testDelegate(String str) {
                log.error("SystemFeignClient testDelegate error",cause);
                return null;
            }
        };
    }
}
