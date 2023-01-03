package com.yyl.system.controller;

import com.yyl.base.common.Result;
import com.yyl.base.exception.BusinessException;
import com.yyl.base.exception.CustomException;
import com.yyl.system.dto.UserDTO;
import com.yyl.system.entity.User;
import com.yyl.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/14 17:42
 */
@RequestMapping("/sys/user")
@RestController
@Slf4j
@Api(tags="用户管理")
public class SysUserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "创建用户")
    @PostMapping(value = "/add")
    public User add(@Valid @RequestBody UserDTO userDTO){
        User user = userService.add(userDTO);
        return user;
    }

    @ApiOperation(value = "批量操作用户测试")
    @PostMapping(value = "/batchOperate")
    public Result batchOperate(@Valid @RequestBody UserDTO userDTO){
        List<UserDTO> list = new ArrayList();
        list.add(userDTO);
        UserDTO userDTO2 = new UserDTO();
        BeanUtils.copyProperties(userDTO,userDTO2);
        userDTO2.setUsername(userDTO.getUsername()+"2");
        userDTO2.setLoginname(userDTO.getLoginname()+"2");
        list.add(userDTO2);
        userService.operateBatchList(list);
        userDTO.setUsername(userDTO.getUsername()+"Param");
        userDTO.setLoginname(userDTO.getLoginname()+"Param");
        userDTO2.setUsername(userDTO2.getUsername()+"Param");
        userDTO2.setLoginname(userDTO2.getLoginname()+"Param");
        userService.operateBatchListByParam(list);
        userDTO.setUsername(userDTO.getUsername()+"Array");
        userDTO.setLoginname(userDTO.getLoginname()+"Array");
        userDTO2.setUsername(userDTO2.getUsername()+"Array");
        userDTO2.setLoginname(userDTO2.getLoginname()+"Array");
        userService.operateBatchArray(list);
        return Result.ok();
    }

    @PostMapping(value = "/addNormalExceptionHandler")
    public User addNormalExceptionHandler(@RequestBody UserDTO userDTO) throws Exception{
        Thread.sleep(1000);
        log.info("addNormalExceptionHandler");
        throw new CustomException("自定义异常");
    }

    @PostMapping(value = "/testGetException")
    public User testGetException(String name)throws Exception{
        log.info("testGetException");
        throw new BusinessException("业务异常");
    }
    @PostMapping(value = "/testPostException")
    public User testPostException(@RequestBody UserDTO userDTO){
        log.info("testPostException");
        throw new BusinessException("业务异常");
    }

    @RequestMapping(value = "/testGetRetry")
    public User testGetRetry(String name) throws Exception{
        log.info("testGetRetry");
        Thread.sleep(4000);
        return new User();
    }

    @PostMapping(value = "/testPostRetry")
    public User testPostRetry(@RequestBody UserDTO userDTO) throws Exception{
        log.info("testPostRetry");
        Thread.sleep(4000);
        return new User();
    }

    @RequestMapping(value = "/testTimeout")
    public User testTimeout(@RequestBody UserDTO userDTO) throws Exception{
        log.info("testTimeout");
        Thread.sleep(4000);
        return new User();
    }

    @RequestMapping(value = "/testDelegate")
    public User testDelegate(String str) throws Exception{
        log.info("testDelegate");
        Thread.sleep(1000);
        if(StringUtils.isEmpty(str)){
            throw new RuntimeException("无效参数");
        }
        return new User();
    }

}
