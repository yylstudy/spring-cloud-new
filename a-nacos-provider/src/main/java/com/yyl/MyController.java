package com.yyl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2020-12-09
 */
@RestController
@RefreshScope
public class MyController {
    @Value("${age:}")
    private String age;

    @RequestMapping("/test1")
    public String test1(){
        System.out.println("age:"+age);
        return "success";
    }
}
