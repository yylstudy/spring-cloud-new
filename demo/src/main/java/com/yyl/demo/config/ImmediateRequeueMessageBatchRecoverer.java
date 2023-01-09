package com.yyl.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.ImmediateRequeueAmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.MessageBatchRecoverer;

import java.util.List;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023/1/9 16:02
 */
@Slf4j
public class ImmediateRequeueMessageBatchRecoverer implements MessageBatchRecoverer {
    @Override
    public void recover(List<Message> messages, Throwable cause) {
        for(Message message:messages){
            log.warn("Retries exhausted for message " + message + "; requeuing...", cause);
        }
        throw new ImmediateRequeueAmqpException(cause);
    }
}
