package com.yyl.demo.config;

import com.yyl.base.context.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.ImmediateRequeueMessageRecoverer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecovererWithConfirms;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;


/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023/1/3 10:47
 */
@Configuration
@Slf4j
public class RabbitmqConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory;
    @PostConstruct
    public void init(){
        //发送端确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("发送端消息确认,correlationData:{},ack:{},cause:{}",correlationData, ack, cause);
        });
        rabbitTemplate.setReturnsCallback(returnedMessage->{
            log.warn("消息未被正确投递：{}",returnedMessage);
        });
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
//        simpleRabbitListenerContainerFactory.setBatchListener(true);
    }

    /**
     * amqp采用jackson序列化
     * @return
     */
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    /**
     * 消费失败队列
     * @return
     */
    @Bean
    public Queue consumerErrorQueue(){
        Queue queue = QueueBuilder.durable("consumer_error_queue").build();
        return queue;
    }

    /**
     * 消费失败绑定
     * @return
     */
    @Bean
    public Binding consumerErrorBinding(){
        Binding binding = new Binding("consumer_error_queue", Binding.DestinationType.QUEUE,
                "consumer_error_exchange", "t_key", null);
        return binding;
    }

    /**
     * 消费失败交换器
     * @return
     */
    @Bean
    public Exchange consumerErrorExchange(){
        Exchange exchange =ExchangeBuilder.directExchange("consumer_error_exchange").build();
        return exchange;
    }

    /**
     * 重新发布且待生产消息确认的消息回收器
     * @return
     */
    @Bean
    public MessageRecoverer republishMessageRecovererWithConfirms() {
        return new RepublishMessageRecovererWithConfirms(rabbitTemplate,"consumer_error_exchange",
                "t_key", CachingConnectionFactory.ConfirmType.CORRELATED);
    }

    /**
     * 马上重入队列消息回收器
     * @return
     */
    @Bean
    @Primary
    public MessageRecoverer immediateRequeueMessageRecoverer() {
        return new ImmediateRequeueMessageRecoverer();
    }

    /**
     * 马上重入队列消息回收器
     * @return
     */
    @Bean
    public MessageRecoverer immediateRequeueMessageBatchRecoverer() {
        return new ImmediateRequeueMessageBatchRecoverer();
    }



    @Bean
    public Queue tQueue(){
        Queue queue = QueueBuilder.durable("t_queue").build();
        return queue;
    }

    @Bean
    public Binding binding(){
        Binding binding = new Binding("t_queue", Binding.DestinationType.QUEUE,
                "test_change", "t_key", null);
        return binding;
    }

    @Bean
    public Queue tQueue3(){
        Queue queue = QueueBuilder.durable("t_queue3").build();
        return queue;
    }

    @Bean
    public Binding binding3(){
        Binding binding = new Binding("t_queue3", Binding.DestinationType.QUEUE,
                "test_change", "t_key3", null);
        return binding;
    }
}
