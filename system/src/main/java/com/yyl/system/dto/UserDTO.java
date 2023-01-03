package com.yyl.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/15 15:48
 */
@Data
public class UserDTO implements Serializable {
    private Long id;
    @ApiModelProperty(value = "用户名",required = true)
    @NotEmpty(message = "用户名不能为空")
    private String username;
    @ApiModelProperty(value = "登录名",required = true)
    @NotEmpty(message = "登录名不能为空")
    private String loginname;
    @NotEmpty(message = "密码不能为空")
    @ApiModelProperty(value = "密码",required = true)
    private String password;
}
