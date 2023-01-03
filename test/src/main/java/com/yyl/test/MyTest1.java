package com.yyl.test;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.yyl.base.common.Result;
import com.yyl.base.util.HttpUtil;
import com.yyl.base.util.JsonUtil;
import com.yyl.demo.dto.UserRepDTO;
import com.yyl.system.dto.LoginRqDTO;
import com.yyl.system.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/12/5 14:23
 */
@Slf4j
public class MyTest1 {
    public static final String ACCESS_TOKEN = "access_token";
    @Test
    public void test1() throws Exception{
        //token校验
        UserDTO userDTO = new UserDTO();
        Result<?> result = HttpUtil.doPostForObject("http://localhost:9998/sys/user/add",userDTO, Result.class);
        log.info("result:{}",result);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("token为空",result.getMessage());
        String token = getToken();

//        HttpUtil.doPostForObject("http://localhost:9998/demo/user/addNormalExceptionHandler",userDTO, UserRepDTO.class);
//        entity = HttpUtil.doPostForEntity("http://localhost:9998/demo/user/add",userDTO,header, Result.class);
    }

    /**
     * 批量填充测试、参数校验
     */
    @Test
    public void test2(){
        String token = getToken();
        Map<String,String> header = new HashMap<>();
        header.put(ACCESS_TOKEN,token);
        UserDTO userDTO = new UserDTO();
        ResponseEntity<Result> entity = HttpUtil.doPostForEntity("http://localhost:9998/sys/user/add",userDTO,header, Result.class);
        log.info("entity:{}",entity);
        Assert.assertFalse(entity.getBody().isSuccess());
        Assert.assertTrue(entity.getBody().getMessage().contains("登录名不能为空"));
        Assert.assertTrue(entity.getBody().getMessage().contains("用户名不能为空"));
        Assert.assertTrue(entity.getBody().getMessage().contains("密码不能为空"));
        userDTO.setLoginname("yyl");
        userDTO.setPassword("cqt@1234");
        userDTO.setUsername("yyl");
        //创建用户
        UserRepDTO userRepDTO = HttpUtil.doPostForObject("http://localhost:9998/sys/user/add",userDTO,header, UserRepDTO.class);
        log.info("userRepDTO:{}",userRepDTO);
        userDTO.setLoginname("yyl1");
        userDTO.setUsername("yyl1");
        //批量创建用户
        Result result = HttpUtil.doPostForObject("http://localhost:9998/sys/user/batchOperate",userDTO,header, Result.class);
        log.info("result:{}",result);
        Assert.assertTrue(result.isSuccess());
    }

    /**
     * feign测试
     */
    @Test
    public void test3(){
        String token = getToken();
        Map<String,String> header = new HashMap<>();
        header.put(ACCESS_TOKEN,token);
        UserDTO userDTO = new UserDTO();
        //spring mvc全局异常处理器和openfeign配合的注意点
        HttpUtil.doPostForObject("http://localhost:9998/demo/user/addNormalExceptionHandler",userDTO,header, UserRepDTO.class);
        //get请求响应状态码500重试
        UserRepDTO userRepDTO = HttpUtil.doPostForObject("http://localhost:9998/demo/user/testGetException?name=yyl","{}",header, UserRepDTO.class);
        //post请求响应状态码500不重试
        HttpUtil.doPostForObject("http://localhost:9998/demo/user/testPostException",userDTO,header, UserRepDTO.class);
        //get超时重试
        ResponseEntity<UserRepDTO> entity = HttpUtil.doPostForEntity("http://localhost:9998/demo/user/testGetRetry?name=yyl","{}",header, UserRepDTO.class);
        log.info("entity:{}",entity);
        //post超时依然重试
        HttpUtil.doPostForObject("http://localhost:9998/demo/user/testPostRetry",userDTO,header, UserRepDTO.class);
    }

    /**
     * 熔断测试：熔断开启
     */
    @Test
    public void test4(){
        String token = getToken();
        Map<String,String> header = new HashMap<>();
        header.put(ACCESS_TOKEN,token);
        UserDTO userDTO = new UserDTO();
        HttpUtil.doPostForObject("http://localhost:9998/demo/testDelegate",userDTO,header, UserRepDTO.class);
    }
    /**
     * 熔断测试：熔断关闭
     */
    @Test
    public void test5(){
        String token = getToken();
        Map<String,String> header = new HashMap<>();
        header.put(ACCESS_TOKEN,token);
        UserDTO userDTO = new UserDTO();
        HttpUtil.doPostForObject("http://localhost:9998/demo/testRecovery",userDTO,header, UserRepDTO.class);
    }
    /**
     * spring cloud gateway自定义路由信息
     * 将/custom/**的请求信息转发到demo上，路由配置信息为
     * {
     * 	"id": "custom",
     *     "predicates": [{
     *       "name": "Path",
     *       "args": {
     *         "_genkey_0": "/custom/**"
     *       }
     *     }],
     * 	"filters": [{"args":{"parts":1},"name":"StripPrefix"}],
     * 	"uri": "lb://demo",
     * 	"order": 0
     * }
     *
     */
    @Test
    public void test6(){
        String token = getToken();
        Map<String,String> header = new HashMap<>();
        header.put(ACCESS_TOKEN,token);
        UserDTO userDTO = new UserDTO();
        String userRepDTO = HttpUtil
                .doPostForObject("http://localhost:9998/custom/testRecovery",userDTO,header, String.class);
        log.info("userRepDTO:{}",userRepDTO);
    }

    /**
     * spring cloud gateway自定义路由超时时间
     * 将/custom/**的请求信息转发到demo上，路由配置信息为
     * {
     * 	"id": "custom",
     *     "predicates": [{
     *       "name": "Path",
     *       "args": {
     *         "_genkey_0": "/custom/**"
     *       }
     *     }],
     *   "metadata":{
     *     "response-timeout":6000,
     *     "connect-timeout":2000
     *   },
     * 	"filters": [{"args":{"parts":1},"name":"StripPrefix"}],
     * 	"uri": "lb://demo",
     * 	"order": 0
     * }
     */
    @Test
    public void test7(){
        String token = getToken();
        Map<String,String> header = new HashMap<>();
        header.put(ACCESS_TOKEN,token);
        UserDTO userDTO = new UserDTO();
        long t1 = System.currentTimeMillis();
        String userRepDTO = HttpUtil
                .doPostForObject("http://localhost:9998/custom/testGatewayTimeout",userDTO,header, String.class);
        long t2 = System.currentTimeMillis();
        log.info("time:{}",(t2-t1));
        log.info("userRepDTO:{}",userRepDTO);
    }

    /**
     * 测试网关熔断降级，这里需要注意的是sentinel暂时不支持针对 4xx、5xx 等状态码记为错误
     * 所以如果是异常数或者异常比例熔断，那么如果下游服务配置了全局异常处理器，那么是不会走降级策略的
     * 这边为了模拟，所以在testGatewayDelegate接口中进行了sleep，他的时间比此路由超时时间长，以此来模拟gateway降级
     */
    @Test
    public void test8() throws Exception{
        String token = getToken();
        Map<String,String> header = new HashMap<>();
        header.put(ACCESS_TOKEN,token);
        UserDTO userDTO = new UserDTO();
        ExecutorService pool = Executors.newFixedThreadPool(4);
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for(int i=0;i<100;i++){
            pool.execute(()->{
                try{
                    long t1 = System.currentTimeMillis();
                    ResponseEntity<String> entity = HttpUtil
                            .doPostForEntity("http://localhost:9998/custom/testGatewayDelegate",userDTO,header, String.class);
                    long t2 = System.currentTimeMillis();
                    log.info("time:{}",(t2-t1));
                    log.info("result body:{}",entity.getBody());
                    log.info("result code:{}",entity.getStatusCode());
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
    }

    /**
     * 测试网关限流，限流负责为5s内只允许一个请求通过
     * [{
     *     "resource":"ReactiveCompositeDiscoveryClient_demo",
     *     "resourceMode":0,
     *     "grade":1,
     *     "count":1.0,
     *     "intervalSec":5,
     *     "controlBehavior":0,
     *     "burst":0,
     *     "maxQueueingTimeoutMs":500,
     *     "paramItem":null
     *     }]
     */
    @Test
    public void test9() throws Exception{
        String token = getToken();
        Map<String,String> header = new HashMap<>();
        header.put(ACCESS_TOKEN,token);
        UserDTO userDTO = new UserDTO();
        ExecutorService pool = Executors.newFixedThreadPool(4);
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for(int i=0;i<100;i++){
            pool.execute(()->{
                try{
                    long t1 = System.currentTimeMillis();
                    ResponseEntity<String> entity = HttpUtil
                            .doPostForEntity("http://localhost:9998/demo/testGatewayFlow",userDTO,header, String.class);
                    long t2 = System.currentTimeMillis();
                    log.info("time:{}",(t2-t1));
                    log.info("result body:{}",entity.getBody());
                    log.info("result code:{}",entity.getStatusCode());
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
    }

    /**
     * 测试url参数限流
     */
    @Test
    public void test10() throws Exception{
        String token = getToken();
        Map<String,String> header = new HashMap<>();
        header.put(ACCESS_TOKEN,token);
        UserDTO userDTO = new UserDTO();
        ExecutorService pool = Executors.newFixedThreadPool(4);
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for(int i=0;i<100;i++){
            Random random = new Random();
            pool.execute(()->{
                try{
                    long t1 = System.currentTimeMillis();
                    ResponseEntity<String> entity = HttpUtil
                            .doPostForEntity("http://localhost:9998/custom/testGatewayFlow?flowparam="+random.nextInt(10000),userDTO,header, String.class);
                    long t2 = System.currentTimeMillis();
                    log.info("time:{}",(t2-t1));
                    log.info("result body:{}",entity.getBody());
                    log.info("result code:{}",entity.getStatusCode());
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
    }

    /**
     * 网关全局异常处理器
     */
    @Test
    public void test11() throws Exception{
        String token = getToken();
        Map<String,String> header = new HashMap<>();
        header.put(ACCESS_TOKEN,token);
        UserDTO userDTO = new UserDTO();
        long t1 = System.currentTimeMillis();
        ResponseEntity<String> entity = HttpUtil
                .doPostForEntity("http://localhost:9998/custom3/testGatewayFlow",userDTO,header, String.class);
        long t2 = System.currentTimeMillis();
        log.info("time:{}",(t2-t1));
        log.info("result body:{}",entity.getBody());
        log.info("result code:{}",entity.getStatusCode());
    }

    private String getToken(){
        LoginRqDTO loginRqDTO = new LoginRqDTO();
        loginRqDTO.setLoginname("admin");
        loginRqDTO.setPassword("admin");
        Result<Map> result = HttpUtil.doPostForObject("http://localhost:9998/sys/login",loginRqDTO, Result.class);
        return (String)result.getResult().get("token");
    }


    public static void main(String[] args) {
        GatewayFlowRule gatewayFlowRule = new GatewayFlowRule();
        gatewayFlowRule.setResource("ReactiveCompositeDiscoveryClient_demo");
        gatewayFlowRule.setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_ROUTE_ID);
        gatewayFlowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        gatewayFlowRule.setCount(1);
        gatewayFlowRule.setIntervalSec(5);
        gatewayFlowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        GatewayParamFlowItem paramItem = new GatewayParamFlowItem();
        //来源IP
        paramItem.setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM);
        paramItem.setFieldName("flowparam");
        paramItem.setPattern("");
        paramItem.setMatchStrategy(SentinelGatewayConstants.PARAM_MATCH_STRATEGY_EXACT);
        gatewayFlowRule.setParamItem(paramItem);
//        gatewayFlowRule.setBurst();
        System.out.println(JsonUtil.toJSONString(gatewayFlowRule));
//        degradeRule.setSlowRatioThreshold()
    }


}
