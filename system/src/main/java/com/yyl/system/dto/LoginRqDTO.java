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
@ApiModel(value="登录对象", description="登录对象")
@Data
public class LoginRqDTO {
    @ApiModelProperty(value = "账号")
    @NotEmpty(message = "登录名不能为空")
    private String loginname;
    @ApiModelProperty(value = "密码")
    @NotEmpty(message = "密码不能为空")
    private String password;
}
