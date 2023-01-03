package com.yyl.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/14 18:04
 */
@ApiModel(value="登录返回对象", description="登录返回对象")
@Data
public class LoginRpDTO {
    @ApiModelProperty(value = "token")
    private String token;
}
