package com.yyl.system.controller;

import com.yyl.base.common.Result;
import com.yyl.system.dto.LoginRpDTO;
import com.yyl.system.dto.LoginRqDTO;
import com.yyl.system.entity.User;
import com.yyl.system.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/14 17:45
 */
@RequestMapping("/sys/")
@RestController
@Slf4j
@Api(tags="登录管理")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @ApiOperation("登录接口")
    @PostMapping(value = "/login")
    public Result<LoginRpDTO> login(@Valid @RequestBody LoginRqDTO loginRqDTO){
        return loginService.login(loginRqDTO);
    }

    @ApiOperation("token验证接口")
    @GetMapping(value = "/verifyToken")
    public boolean verifyToken(@RequestParam("token") String token) throws Exception{
//        Thread.sleep(5000);
        return loginService.verifyToken(token);
    }

    @ApiOperation("根据token获取用户")
    @GetMapping(value = "/getUserByToken")
    public User getUserByToken(@RequestParam("token") String token){
        User user = loginService.getUserByToken(token);
        return user;
    }
}
