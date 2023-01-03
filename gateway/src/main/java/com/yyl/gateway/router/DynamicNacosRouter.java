package com.yyl.gateway.router;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yyl.gateway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/15 17:27
 */
@Component
public class DynamicNacosRouter {
    private static ObjectMapper mapper = new ObjectMapper();
    private final static Logger log = LoggerFactory.getLogger(DynamicNacosRouter.class);
    public static final long DEFAULT_TIMEOUT = 30000;
    @Value("${gateway.route.config.data-id}")
    private String routeDataId;
    @Value("${gateway.route.config.group}")
    private String routeGroup;
    @Autowired
    private InMemoryRouteDefinitionRepository repository;
    @Autowired
    private NacosConfigManager nacosConfigManager;
    @PostConstruct
    public void initRoute(){
        ConfigService configService = nacosConfigManager.getConfigService();
        try{
            String routeStr = configService.getConfig(routeDataId,routeGroup,DEFAULT_TIMEOUT);
            if(!StringUtils.hasText(routeStr)){
                log.warn("未查找到路由信息");
                return ;
            }
            List<RouteDefinition> routeDefinitions = JsonUtil.parseList(routeStr, RouteDefinition.class);
            routeDefinitions.stream().forEach(this::save);
            configService.addListener(routeDataId,routeGroup,new Listener(){
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    try{
                        List<RouteDefinition> routeDefinitions = JsonUtil.parseList(configInfo, RouteDefinition.class);
                        routeDefinitions.stream().forEach(routeDefinition -> save(routeDefinition));
                    }catch (Exception e){
                        log.error("从nacos刷新路由失败",e);
                    }
                }
            });
        }catch (Exception e){
            log.error("从nacos获取路由信息失败",e);
            System.exit(0);
        }
    }
    public synchronized void save(RouteDefinition definition) {
        log.info("gateway save route {}", definition);
        repository.save(Mono.just(definition)).subscribe();
    }
}
