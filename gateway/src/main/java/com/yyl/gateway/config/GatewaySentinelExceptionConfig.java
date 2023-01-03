package com.yyl.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.yyl.gateway.common.SentinelErrorInfoEnum;
import io.netty.util.internal.ObjectUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023/1/1 14:44
 */
@Configuration
public class GatewaySentinelExceptionConfig {
    @PostConstruct
    public void init(){
        BlockRequestHandler blockRequestHandler = (serverWebExchange, ex) -> {
            String msg;
            SentinelErrorInfoEnum errorInfoEnum = SentinelErrorInfoEnum.getErrorByException(ex);
            if (errorInfoEnum!=null) {
                msg = errorInfoEnum.getError();
            } else {
                msg = "未知限流降级";
            }
            HashMap<String, String> map = new HashMap(5);
            map.put("success","false");
            map.put("timestamp",System.currentTimeMillis()+"");
            map.put("code", HttpStatus.TOO_MANY_REQUESTS.value()+"");
            map.put("message", msg);
            //自定义异常处理
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(map));
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }
}
