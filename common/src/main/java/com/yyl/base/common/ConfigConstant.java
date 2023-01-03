package com.yyl.base.common;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/15 16:33
 */

public class ConfigConstant {
    /**
     * http连接超时时间配置
     */
    public static final String REST_CONNECT_TIMEOUT = "rest.connectTimeout";
    /**
     * http读取超时时间配置
     */
    public static final String REST_READ_TIMEOUT = "rest.readTimeout";
    /**
     * 从连接池获取连接的超时时间配置
     */
    public static final String REST_CONNECT_REQUEST_TIMEOUT = "rest.connectRequestTimeout";
    /**
     * 最大连接数
     */
    public static final String REST_MAX_TOTAL = "rest.maxTotal";
    /**
     * 每个路由的最大连接数
     */
    public static final String REST_MAX_PER_ROUTE = "rest.maxPerRoute";
    /**
     * web过期时间配置
     */
    public static final String WEB_JWT_EXPIRE_TIME = "web.jwt.expireTime";


}
