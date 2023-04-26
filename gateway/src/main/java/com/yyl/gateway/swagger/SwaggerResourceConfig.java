package com.yyl.gateway.swagger;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023/1/10 20:36
 */
@Slf4j
@Component
@Primary
public class SwaggerResourceConfig implements SwaggerResourcesProvider {
    @Autowired
    private  RouteLocator routeLocator;
    @Value("${spring.application.name}")
    private String selfApplicationName;
    @Autowired
    private NacosServiceManager nacosServiceManager;
    @Autowired
    private DiscoveryClient discoveryClient;

    private static final String SWAGGER2URL = "/v2/api-docs";

    private String AUTO_ROUTE_PREFIX = "ReactiveCompositeDiscoveryClient_";

    private  GatewayProperties gatewayProperties;

    @Override
    public List<SwaggerResource> get() {
        List<String> serviceList =  discoveryClient.getServices();
//            List<ServiceInstance> instances = serviceList.stream()
//                    .flatMap(service->discoveryClient.getInstances(service).stream()).collect(Collectors.toList());
        List<SwaggerResource> resources = new ArrayList<>();
        List<String> routes = new ArrayList<>();
        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
        Set<String> set = new HashSet<>();
        routeLocator.getRoutes().filter(route->{
            String routeId = route.getId();
//            return !(AUTO_ROUTE_PREFIX+selfApplicationName).equals(routeId)&&serviceList.stream().anyMatch(service->service
//                    .equals(routeId)||(AUTO_ROUTE_PREFIX+service).equals(routeId));
            return !(AUTO_ROUTE_PREFIX+selfApplicationName).equals(routeId)&&serviceList.stream()
                    .anyMatch(service->(AUTO_ROUTE_PREFIX+service).equals(routeId));
        }).subscribe(route->{
            List<String> patterns = new ArrayList<>();
            route.getPredicate().accept(hasConfig->patterns.addAll(((PathRoutePredicateFactory
                    .Config)hasConfig.getConfig()).getPatterns()));
            String swaggerUrl = patterns.get(0).replace("/**",SWAGGER2URL);
            if(!set.contains(swaggerUrl)){
                SwaggerResource swaggerResource = new SwaggerResource();
                swaggerResource.setUrl(swaggerUrl);
                swaggerResource.setSwaggerVersion("2.0");
                swaggerResource.setName(route.getUri().getHost());
                resources.add(swaggerResource);
            }
        });
        return resources;
    }
}
