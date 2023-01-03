package com.yyl.system.controller;

import com.yyl.system.service.SysLogService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/11/14 17:42
 */
@RequestMapping("/sys/log")
@RestController
@Slf4j
@Api(tags="日志管理")
public class SysLogController {
    @Autowired
    private SysLogService sysLogService;
}
