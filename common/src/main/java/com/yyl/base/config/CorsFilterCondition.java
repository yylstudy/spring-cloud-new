package com.yyl.base.config;

import com.yyl.base.common.CommonConstant;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023/5/5 15:43
 */

public class CorsFilterCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Object object = context.getEnvironment().getProperty(CommonConstant.CLOUD_SERVER_KEY);
        //如果没有服务注册发现的配置 说明是单体应用 则加载跨域配置 返回true
        if(object==null){
            return true;
        }
        return false;
    }
}
