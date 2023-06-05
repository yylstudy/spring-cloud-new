package com.yyl.system.dto;

import com.yyl.base.common.SexEnum;
import com.yyl.base.validation.Email;
import com.yyl.base.validation.Enum;
import com.yyl.base.validation.Mobile;
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
    @NotEmpty(message = "邮箱不能为空")
    @ApiModelProperty(value = "邮箱",required = true)
    @Email
    private String email;
    @NotEmpty(message = "手机号码不能为空")
    @ApiModelProperty(value = "手机号码",required = true)
    @Mobile
    private String phone;
    @NotEmpty(message = "性别不能为空")
    @ApiModelProperty(value = "性别",required = true)
    @Enum(value = SexEnum.class,message = "性别只能为{value}")
    private String sex;
}
