package com.yyl.base.common;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/14 17:56
 */

public class CommonConstant {
    /**
     * http请求成功code
     */
    public static final int HTTP_RESP_SUCCESS = 200;
    /**
     * http请求失败服务器异常code
     */
    public static final int HTTP_SERVER_ERROR = 500;
    /**
     * 标准时间格式
     */
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * web jwt默认过期时间
     */
    public static final long DEFAULT_WEB_JWT_EXPIRE_TIME = 60*60*1000;
    /**
     * 用户token redis key前缀
     */
    public static final String PREFIX_TOKEN = "user:token:";
    /**
     * http header中存放的key
     */
    public static final String ACCESS_TOKEN = "access_token";
    /**
     * 创建时间field名
     */
    public static final String CREATE_TIME_FIELD = "createTime";
    /**
     * 创建人field名
     */
    public static final String CREATE_BY_FIELD = "createBy";
    /**
     * 更新时间field名
     */
    public static final String UPDATE_TIME_FIELD = "updateTime";
    /**
     * 更新人field名
     */
    public static final String UPDATE_BY_FIELD = "updateBy";
    /**
     * 更新人field名
     */
    public static final String FEIGN_CALL = "updateBy";
}
