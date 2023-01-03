package com.yyl.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/15 15:48
 */
@Data
@TableName("sys_user")
public class User implements Serializable {
    private Long id;
    private String username;
    private String loginname;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String salt;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
    @TableField(fill = FieldFill.INSERT)
    private String mybatisAutoFillUsername;
}
