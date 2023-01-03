package com.yyl.base.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/14 17:48
 */
@ApiModel(value="接口返回对象", description="接口返回对象")
@Data
public class Result<T> implements Serializable {
    /**
     * 成功标志
     */
    @ApiModelProperty(value = "成功标志")
    private boolean success = true;
    /**
     * 返回代码
     */
    @ApiModelProperty(value = "返回代码")
    private Integer code = 0;
    /**
     * 返回处理消息
     */
    @ApiModelProperty(value = "返回处理消息")
    private String message = "";
    /**
     * 返回数据对象 data
     */
    @ApiModelProperty(value = "返回数据对象")
    private T result;
    /**
     * 时间戳
     */
    @ApiModelProperty(value = "时间戳")
    private long timestamp = System.currentTimeMillis();

    public static<T> Result<T> ok() {
        return ok(null);
    }

    public static<T> Result<T> ok(T t) {
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.HTTP_RESP_SUCCESS);
        result.setResult(t);
        return result;
    }

    public static<T> Result<T> error(String message) {
        return error(CommonConstant.HTTP_SERVER_ERROR,message);
    }

    public static<T> Result<T> error(int code,String message) {
        Result<T> result = new Result<T>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

}
