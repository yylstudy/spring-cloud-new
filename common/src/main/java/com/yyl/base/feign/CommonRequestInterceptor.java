package com.yyl.base.feign;

import com.yyl.base.common.CommonConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description feign token传递的拦截器
 * @createTime 2022/11/22 10:37
 */
@ConditionalOnClass(RequestInterceptor.class)
@Component
public class CommonRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes!=null){
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader(CommonConstant.ACCESS_TOKEN);
            if(StringUtils.hasText(token)){
                template.header(CommonConstant.ACCESS_TOKEN,token);
            }
        }
    }
}
