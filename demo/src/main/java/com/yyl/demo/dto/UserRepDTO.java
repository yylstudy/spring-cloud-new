package com.yyl.demo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/12/7 16:57
 */
@Data
public class UserRepDTO implements Serializable {
    private Long id;
    private String username;
    private String loginname;
    private String createTime;
    private String createBy;
    private String updateTime;
    private String updateBy;
}