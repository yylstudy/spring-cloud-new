package com.yyl.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yyl.system.dto.UserDTO;
import com.yyl.system.entity.User;

import java.util.List;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/15 15:47
 */

public interface UserService extends IService<User> {
    /**
     * 根据登录名获取用户
     * @param loginname
     * @return
     */
    User getByLoginname(String loginname);

    User add(UserDTO userDTO);

    void operateBatchList(List<UserDTO> userDTOS);

    void operateBatchListByParam(List<UserDTO> userDTOS);

    void operateBatchArray(List<UserDTO> userDTOS);
}
