package com.yyl.base.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.yyl.base.util.JwtUtil;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/12/13 15:33
 */
@Component
@ConditionalOnClass(Intercepts.class)
public class AutoFillMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Object rawValue = metaObject.getValue("mybatisAutoFillUsername");
        if(rawValue==null){
            this.strictInsertFill(metaObject, "mybatisAutoFillUsername", String.class, JwtUtil.getLoginname());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
