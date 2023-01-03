package com.yyl.demo.feignclients;

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
@FeignClient(value = "system",contextId = "system",fallbackFactory = SystemFallbackFactory.class,configuration = SystemFeignClientConfigure.class)
public interface SystemFeignClient {

    @PostMapping(value = "sys/user/add")
    UserRepDTO add( @RequestBody UserReqDTO userReqDTO);

    @PostMapping(value = "sys/user/addNormalExceptionHandler")
    UserRepDTO addNormalExceptionHandler( @RequestBody UserReqDTO userReqDTO);

    @GetMapping(value = "sys/user/testGetException")
    UserRepDTO testGetException( String name);

    @PostMapping(value = "sys/user/testPostException")
    UserRepDTO testPostException( @RequestBody UserReqDTO userReqDTO);

    @GetMapping(value = "sys/user/testGetRetry")
    UserRepDTO testGetRetry(String name);

    @PostMapping(value = "sys/user/testPostRetry")
    UserRepDTO testPostRetry(@RequestBody UserReqDTO userDTO);

    @PostMapping(value = "sys/user/testDelegate")
    UserRepDTO testDelegate( @RequestParam("str") String str);

}
