package com.yyl.demo.controller;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreaker;
import com.rabbitmq.client.Channel;
import com.yyl.demo.dto.UserRepDTO;
import com.yyl.demo.dto.UserReqDTO;
import com.yyl.demo.feignclients.SystemFeignClient;
import com.yyl.demo.feignclients.SystemFeignClient2;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/12/7 16:59
 */
@RestController
@Slf4j
public class DemoController {
    @Autowired
    private SystemFeignClient systemFeignClient;
    @Autowired
    private SystemFeignClient2 systemFeignClient2;

    @RequestMapping("user/add")
    public UserRepDTO add(@RequestBody UserReqDTO userReqDTO){
        UserRepDTO userRepDTO = systemFeignClient.add(userReqDTO);
        log.info("userRepDTO:{}",userRepDTO);
        return userRepDTO;
    }

    /**
     * system接口抛出CustomException system的全局异常处理捕获这个异常，但是请求状态码还是200，不会走其fallback的实现
     * @param userReqDTO
     * @return
     */
    @RequestMapping("user/addNormalExceptionHandler")
    public UserRepDTO addNormalExceptionHandler(@RequestBody UserReqDTO userReqDTO){
        UserRepDTO userRepDTO = systemFeignClient.addNormalExceptionHandler(userReqDTO);
        Assert.assertNull(userRepDTO.getId());
        Assert.assertNull(userRepDTO.getUsername());
        Assert.assertNull(userRepDTO.getLoginname());
        Assert.assertNull(userRepDTO.getCreateTime());
        Assert.assertNull(userRepDTO.getCreateBy());
        Assert.assertNull(userRepDTO.getUpdateTime());
        Assert.assertNull(userRepDTO.getUpdateBy());
        log.info("userRepDTO:{}",userRepDTO);
        return userRepDTO;
    }

    /**
     * get请求响应状态码500重试
     * @param name
     * @return
     */
    @RequestMapping("user/testGetException")
    public UserRepDTO testGetException(String name){
        long t1 = System.currentTimeMillis();
        UserRepDTO userRepDTO = systemFeignClient.testGetException(name);
        Assert.assertNull(userRepDTO);
        long t2 = System.currentTimeMillis();
        log.info("testGetException time:{}",(t2-t1));
        return userRepDTO;
    }
    /**
     * post请求响应状态码500不重试
     * @param userReqDTO
     * @return
     */
    @RequestMapping("user/testPostException")
    public UserRepDTO testPostException(@RequestBody UserReqDTO userReqDTO){
        long t1 = System.currentTimeMillis();
        UserRepDTO userRepDTO = systemFeignClient.testPostException(userReqDTO);
        Assert.assertNull(userRepDTO);
        long t2 = System.currentTimeMillis();
        log.info("testPostException time:{}",(t2-t1));
        return userRepDTO;
    }



    /**
     * get超时重试测试
     * @param name
     * @return
     */
    @RequestMapping("user/testGetRetry")
    public UserRepDTO testGetRetry( String name){
        long t1 = System.currentTimeMillis();
        UserRepDTO userRepDTO = systemFeignClient.testGetRetry(name);
        long t2 = System.currentTimeMillis();
        log.info("testGetRetry time:{}",(t2-t1));
        return userRepDTO;
    }

    /**
     * 超时重试测试
     * @param userReqDTO
     * @return
     */
    @RequestMapping("user/testPostRetry")
    public UserRepDTO testPostRetry(@RequestBody UserReqDTO userReqDTO){
        long t1 = System.currentTimeMillis();
        UserRepDTO userRepDTO = systemFeignClient.testPostRetry(userReqDTO);
        long t2 = System.currentTimeMillis();
        log.info("testPostRetry time:{}",(t2-t1));
        return userRepDTO;
    }


    /**
     * 降级测试
     * @param userReqDTO
     * @return
     */
    @RequestMapping("/testDelegate")
    public UserRepDTO testDelegate(@RequestBody UserReqDTO userReqDTO) throws Exception{
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for(int i=0;i<100;i++){
            pool.execute(()->{
                try{
                    long t1 = System.currentTimeMillis();
                    UserRepDTO userRepDTO = systemFeignClient2.testDelegate("");
                    long t2 = System.currentTimeMillis();
                    log.info("testDelegate time:{}",(t2-t1));
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        UserRepDTO userRepDTO = systemFeignClient.testDelegate("");
        return new UserRepDTO();
    }

    /**
     * 恢复测试
     * @param userReqDTO
     * @return
     */
    @RequestMapping("/testRecovery")
    public UserRepDTO testRecovery(@RequestBody UserReqDTO userReqDTO){
        long t1 = System.currentTimeMillis();
        log.info("testRecovery start");
        UserRepDTO userRepDTO = systemFeignClient2.testDelegate("aaaaaa");
        long t2 = System.currentTimeMillis();
        log.info("testRecovery time:{}",(t2-t1));
        return new UserRepDTO();
    }

    /**
     * 测试网关超时
     * @return
     */
    @RequestMapping("/testGatewayTimeout")
    public UserRepDTO testGatewayTimeout() throws Exception{
        Thread.sleep(10000);
        return new UserRepDTO();
    }

    /**
     * 测试网关降级
     * @return
     */
    @RequestMapping("/testGatewayDelegate")
    public UserRepDTO testGatewayDelegate() throws Exception{
        Thread.sleep(4000);
        throw new RuntimeException("hahah");
    }

    /**
     * 测试网关限流
     * @return
     */
    @RequestMapping("/testGatewayFlow")
    public UserRepDTO testGatewayFlow() {
        return new UserRepDTO();
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 测试rabbitmq
     * @return
     */
    @RequestMapping("/testSendMessage")
    public UserRepDTO testSendMessage() {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        for(int i=0;i<1;i++){
            pool.execute(()->{
                String id = UUID.randomUUID().toString().replace("-","");
                CorrelationData correlationData = new CorrelationData(id);
                UserRepDTO userRepDTO = new UserRepDTO();
                String uuid = UUID.randomUUID().toString().replace("-","");
                userRepDTO.setLoginname(uuid);
                rabbitTemplate.convertAndSend("test_change","t_key",userRepDTO,correlationData);
            });
        }
        return new UserRepDTO();
    }

    /**
     * rabbitmq消费
     * @return
     */
    @RabbitListener(queues = "t_queue")
    public void rabbitListener(UserRepDTO userRepDTO, Message message, Channel channel) throws Exception{
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.info("receive deliveryTag:{}, message:{}",deliveryTag,userRepDTO);
        int i = 1/0;
        channel.basicAck(deliveryTag,false);
    }

//    /**
//     * 测试rabbitmq
//     * @return
//     */
//    @RequestMapping("/testbatchSendMessage")
//    public UserRepDTO testbatchSendMessage() {
//        ExecutorService pool = Executors.newFixedThreadPool(20);
//        for(int i=0;i<1;i++){
//            pool.execute(()->{
//                String id = UUID.randomUUID().toString().replace("-","");
//                CorrelationData correlationData = new CorrelationData(id);
//                UserRepDTO userRepDTO = new UserRepDTO();
//                String uuid = UUID.randomUUID().toString().replace("-","");
//                userRepDTO.setLoginname(uuid);
//                rabbitTemplate.convertAndSend("test_change","t_key3",userRepDTO,correlationData);
//            });
//        }
//        return new UserRepDTO();
//    }

//    /**
//     * rabbitmq 批量消费
//     * @return
//     */
//    @RabbitListener(queues = "t_queue3")
//    public void rabbitListener3( List<Message> messages, Channel channel) throws Exception{
////        long deliveryTag = message.getMessageProperties().getDeliveryTag();
////        log.info("receive deliveryTag:{}, message:{}",deliveryTag,userRepDTO);
//        log.info("receive batch message length:{}",messages.size());
////        int i = 1/0;
//        for(Message message:messages){
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//        }
//    }

    /**
     * 获取实时熔断器状态
     * @return
     */
    @RequestMapping("/getCircuitBreaker")
    public CircuitBreaker.State getCircuitBreaker(){
        Field field = ReflectionUtils.findField(DegradeRuleManager.class,"circuitBreakers");
        ReflectionUtils.makeAccessible(field);
        Map<String, List<CircuitBreaker>> map =  (Map<String, List<CircuitBreaker>>)ReflectionUtils.getField(field,null);
        for(Map.Entry<String, List<CircuitBreaker>> entry:map.entrySet()){
            log.info("circuitBreaker name:{}",entry.getKey());
            log.info("circuitBreaker status:{}",entry.getValue().stream().findFirst().get().currentState());
            return entry.getValue().stream().findFirst().get().currentState();
        }
        return null;
    }


}
