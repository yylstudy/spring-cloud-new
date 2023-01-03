package com.yyl.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yyl.base.feign.LeafFeignClient;
import com.yyl.base.util.PasswordUtil;
import com.yyl.system.dto.UserDTO;
import com.yyl.system.entity.User;
import com.yyl.system.mapper.UserMapper;
import com.yyl.system.service.UserService;
import com.yyl.system.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/15 15:47
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private LeafFeignClient leafFeignClient;

    @Override
    public User getByLoginname(String loginname) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getLoginname,loginname);
        return this.getOne(wrapper);
    }

    @Override
    public User add(UserDTO userDTO) {
        User user = new User();
        long id = leafFeignClient.getSnowflakeId();
        String salt = CommonUtil.randomGen(8);
        user.setId(id);
        user.setSalt(salt);
        user.setLoginname(userDTO.getLoginname());
        String password = PasswordUtil.encrypt(userDTO.getLoginname(),userDTO.getPassword(),salt);
        user.setPassword(password);
        user.setUsername(userDTO.getUsername());
        this.baseMapper.insert(user);
        return user;
    }

    @Override
    public void operateBatchList(List<UserDTO> userDTOS) {
        List<User> list =userDTOS.stream().map(userDTO->{
            User user = new User();
            long id = leafFeignClient.getSnowflakeId();
            String salt = CommonUtil.randomGen(8);
            user.setId(id);
            user.setSalt(salt);
            user.setLoginname(userDTO.getLoginname());
            String password = PasswordUtil.encrypt(userDTO.getLoginname(),userDTO.getPassword(),salt);
            user.setPassword(password);
            user.setUsername(userDTO.getUsername());
            return user;
        }).collect(Collectors.toList());
        this.baseMapper.insertBatchList(list);
        try{
            Thread.sleep(1000);
        }catch (Exception e){
        }
        this.baseMapper.updateBatchList(list);
    }


    @Override
    public void operateBatchListByParam(List<UserDTO> userDTOS) {
        List<User> list =userDTOS.stream().map(userDTO->{
            User user = new User();
            long id = leafFeignClient.getSnowflakeId();
            String salt = CommonUtil.randomGen(8);
            user.setId(id);
            user.setSalt(salt);
            user.setLoginname(userDTO.getLoginname());
            String password = PasswordUtil.encrypt(userDTO.getLoginname(),userDTO.getPassword(),salt);
            user.setPassword(password);
            user.setUsername(userDTO.getUsername());
            return user;
        }).collect(Collectors.toList());
        this.baseMapper.insertBatchListByParam(list);
        try{
            Thread.sleep(1000);
        }catch (Exception e){
        }
        this.baseMapper.updateBatchListByParam(list);
        try{
            Thread.sleep(1000);
        }catch (Exception e){
        }
        this.baseMapper.updateBatchListByParam(list);
    }
    @Override
    public void operateBatchArray(List<UserDTO> userDTOS) {
        User[] users =userDTOS.stream().map(userDTO->{
            User user = new User();
            long id = leafFeignClient.getSnowflakeId();
            String salt = CommonUtil.randomGen(8);
            user.setId(id);
            user.setSalt(salt);
            user.setLoginname(userDTO.getLoginname());
            String password = PasswordUtil.encrypt(userDTO.getLoginname(),userDTO.getPassword(),salt);
            user.setPassword(password);
            user.setUsername(userDTO.getUsername());
            return user;
        }).toArray(i->new User[i]);
        this.baseMapper.insertBatchArray(users);
        try{
            Thread.sleep(1000);
        }catch (Exception e){
        }
        this.baseMapper.updateBatchArray(users);
    }


    public static void main(String[] args) {
        String password = PasswordUtil.encrypt("admin","admin","12345678");
        System.out.println(password);
    }
}
