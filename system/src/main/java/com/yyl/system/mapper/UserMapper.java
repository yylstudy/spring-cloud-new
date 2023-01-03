package com.yyl.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyl.system.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/15 15:48
 */

public interface UserMapper extends BaseMapper<User> {
    int insertBatchList(List<User> users);
    int updateBatchList(List<User> users);
    int insertBatchListByParam(@Param("users") List<User> users);
    int updateBatchListByParam(@Param("users") List<User> users);
    int insertBatchArray(User[] userArray);
    int updateBatchArray(User[] userArray);
}
