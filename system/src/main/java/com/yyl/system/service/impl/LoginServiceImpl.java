package com.yyl.system.service.impl;

import com.yyl.base.common.Result;
import com.yyl.base.common.CommonConstant;
import com.yyl.base.common.ConfigConstant;
import com.yyl.base.context.ApplicationContextHolder;
import com.yyl.base.util.PasswordUtil;
import com.yyl.base.common.TokenStatus;
import com.yyl.system.dto.LoginRpDTO;
import com.yyl.system.dto.LoginRqDTO;
import com.yyl.system.entity.User;
import com.yyl.system.service.LoginService;
import com.yyl.system.service.UserService;
import com.yyl.base.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/15 16:02
 */
@Service
@DependsOn("applicationContextHolder")
public class LoginServiceImpl implements LoginService {
    /**
     * 过期时间
     */
    private final static long EXPIRE_TIME = ApplicationContextHolder.getEnvironment()
            .getProperty(ConfigConstant.WEB_JWT_EXPIRE_TIME,Long.class, CommonConstant.DEFAULT_WEB_JWT_EXPIRE_TIME);
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result<LoginRpDTO> login(LoginRqDTO loginRqDTO) {
        User user = userService.getByLoginname(loginRqDTO.getLoginname());
        if(user==null){
            return Result.error("用户不存在");
        }
        String password = PasswordUtil.encrypt(user.getLoginname(),loginRqDTO.getPassword(),user.getSalt());
        if(!user.getPassword().equals(password)){
            return Result.error("密码不正确");
        }
        String token = JwtUtil.sign(user.getLoginname(),user.getPassword());
        String key = CommonConstant.PREFIX_TOKEN+token;
        stringRedisTemplate.opsForValue().set(key,token,EXPIRE_TIME*2, TimeUnit.MILLISECONDS);
        LoginRpDTO loginRpDTO = new LoginRpDTO();
        loginRpDTO.setToken(token);
        return Result.ok(loginRpDTO);
    }

    @Override
    public boolean verifyToken(String token) {
        String loginname = JwtUtil.getLoginname(token);
        User user = userService.getByLoginname(loginname);
        String key = CommonConstant.PREFIX_TOKEN+token;
        String cacheToken = stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.isEmpty(cacheToken)){
            return false;
        }
        TokenStatus tokenStatus = JwtUtil.verify(cacheToken,loginname,user.getPassword());
        if(tokenStatus==TokenStatus.INVALID){
            return false;
        }
        if(tokenStatus==TokenStatus.EXPIRE){
            String newToken = JwtUtil.sign(user.getLoginname(),user.getPassword());
            stringRedisTemplate.opsForValue().set(key,newToken,EXPIRE_TIME*2, TimeUnit.MILLISECONDS);
        }
        return true;
    }

    @Override
    public User getUserByToken(String token) {
        String loginname = JwtUtil.getLoginname(token);
        return userService.getByLoginname(loginname);
    }
}
