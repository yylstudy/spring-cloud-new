package com.yyl.gateway.filter;

import com.yyl.gateway.util.JsonUtil;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description token校验过滤器
 * @createTime 2022/11/15 17:27
 */
@Component
@Slf4j
public class TokenFilter implements GlobalFilter, Ordered {
    public static final String ACCESS_TOKEN = "access_token";
    private PathMatcher pathMatcher = new AntPathMatcher();
    @Autowired
    private WebClient.Builder webClientBuilder;
    /**
     * webclient的底层httpClient，主要设置超时时间
     */
    private HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
            .responseTimeout(Duration.ofSeconds(5));
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        for(String excludeUrl:excludeUrls){
            if(pathMatcher.match(excludeUrl,url)){
                return chain.filter(exchange);
            }
        }
        String token = exchange.getRequest().getHeaders().getFirst(ACCESS_TOKEN);
        if(!StringUtils.hasText(token)){
            return getVoidMono(exchange.getResponse(), 500,"token为空");
        }
        Mono<Void> mono = webClientBuilder.baseUrl("ls://system")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build().get().uri(uriBuilder ->
                uriBuilder.path("/sys/verifyToken").queryParam("token", token).build())
                .retrieve().bodyToMono(Boolean.class)
                .doOnSuccess(match->log.info("token校验结果:{}",match))
                .doOnError(error->{
                    log.error("token request error",error);
                    throw new RuntimeException("token校验失败");
                }).flatMap(match->{
                    if(!match){
                        return getVoidMono(exchange.getResponse(), 500,"无效的token");
                    }
                    return chain.filter(exchange);
                });
        return mono;
    }

    private Mono<Void> getVoidMono(ServerHttpResponse serverHttpResponse, int errorCode, String errorMsg) {
        serverHttpResponse.getHeaders().add("Character-Encoding", "UTF-8");
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        Map<String,Object> result = new HashMap<>();
        result.put("success",false);
        result.put("code",errorCode);
        result.put("message",errorMsg);
        result.put("timestamp",System.currentTimeMillis());
        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JsonUtil.toJSONString(result).getBytes());
        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private final List<String> excludeUrls = new ArrayList(){{
        add("/sys/cas/client/validateLogin");
        add("/sys/randomImage/**");
        add("/sys/checkCaptcha");
        add("/sys/login");
        add("/sys/mLogin");
        add("/sys/logout");
        add("/sys/thirdLogin/**");
        add("/sys/getEncryptedString");
        add("/sys/sms");
        add("/sys/phoneLogin");
        add("/sys/user/checkOnlyUser");
        add("/sys/user/register");
        add("/sys/user/querySysUser");
        add("/sys/user/phoneVerification");
        add("/sys/user/passwordChange");
        add("/auth/2step-code");
        add("/sys/common/static/**");
        add("/sys/common/pdf/**");
        add("/generic/**");
        add("/");
        add("/doc.html");
        add("/**/*.js");
        add("/**/*.css");
        add("/**/*.html");
        add("/**/*.svg");
        add("/**/*.pdf");
        add("/**/*.jpg");
        add("/**/*.png");
        add("/**/*.ico");
        add("/**/*.ttf");
        add("/**/*.woff");
        add("/**/*.woff2");
        add("/druid/**");
        add("/swagger-ui.html");
        add("/swagger**/**");
        add("/webjars/**");
        add( "/v2/**");
        add("/jmreport/**");
        add("/**/*.js.map");
        add("/**/*.css.map");
        add("/bigscreen/**");
        add("/test/bigScreen/**");
        add("/websocket/**");
        add("/newsWebsocket/**");
        add("/monitorData/**");
        add("/vxeSocket/**");
        add("/eoaSocket/**");
        add("/actuator/**");
        add("/*/v2/api-docs/**");
    }};
}
