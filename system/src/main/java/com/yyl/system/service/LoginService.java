package com.yyl.system.service;


import com.yyl.base.common.Result;
import com.yyl.system.dto.LoginRpDTO;
import com.yyl.system.dto.LoginRqDTO;
import com.yyl.system.entity.User;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/15 16:02
 */
public interface LoginService {
    Result<LoginRpDTO> login(LoginRqDTO loginRqDTO);
    boolean verifyToken(String token);
    User getUserByToken(String token);
}
