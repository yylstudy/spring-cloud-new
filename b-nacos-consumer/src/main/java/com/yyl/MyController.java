package com.yyl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2020-12-09
 */
@RestController
public class MyController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/test1")
    public String test1(){
        String result = restTemplate
                .postForObject("http://nacos-provider/test1",null,String.class);
        System.out.println("call nacos-provider result:"+result);
        return "call success";
    }

    @RequestMapping("/test2")
    public String test2(){
        System.out.println(discoveryClient.getServices());
        return "call success";
    }
}
