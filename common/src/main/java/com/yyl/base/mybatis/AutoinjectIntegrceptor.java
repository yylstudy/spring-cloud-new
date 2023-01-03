package com.yyl.base.mybatis;

import com.yyl.base.common.CommonConstant;
import com.yyl.base.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description mybatis拦截器，自动注入创建时间、修改时间；适用于插入、更新、批量插入、批量更新
 *              还有一种方式也可以实现此功能，mybatis-plus的自动填充属性功能，但是那种方式不支持原生mybatis
 * @createTime 2022/11/22 15:05
 */
@ConditionalOnClass(Intercepts.class)
@Component
@Intercepts(@Signature(type= Executor.class,method = "update",args = {MappedStatement.class,Object.class}))
@Slf4j
public class AutoinjectIntegrceptor implements Interceptor {

    private List<List<Object>> insertInfos = new ArrayList(){{
        add(Arrays.asList(CommonConstant.CREATE_TIME_FIELD,Date.class, (Supplier) () -> new Date()));
        add(Arrays.asList(CommonConstant.CREATE_BY_FIELD,String.class, (Supplier) () -> JwtUtil.getLoginname()));
    }};
    private List<List<Object>> updateInfos = new ArrayList(){{
        add(Arrays.asList(CommonConstant.UPDATE_TIME_FIELD,Date.class, (Supplier) () -> new Date()));
        add(Arrays.asList(CommonConstant.UPDATE_BY_FIELD,String.class, (Supplier) () -> JwtUtil.getLoginname()));
    }};

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement)args[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object entity = args[1];
        if(entity==null||(sqlCommandType!=SqlCommandType.INSERT&&sqlCommandType!=SqlCommandType.UPDATE)){
            return invocation.proceed();
        }
        try{
            List fillObjects = new ArrayList<>();
            //mapper方法参数为多个或者（参数个数为1且类型为Collection或者数组）时，具体代码查看ParamNameResolver#getNamedParams方法
            if(entity instanceof MapperMethod.ParamMap){
                Collection collection = ((MapperMethod.ParamMap<?>) entity).values();
                for(Object param:collection){
                    if(param ==null){
                        continue;
                    }
                    //参数为1一个且类型为Collection
                    if (param instanceof Collection) {
                        fillObjects.addAll((Collection)param);
                    }
                    //参数为1一个且类型为数组
                    else if (param.getClass().isArray()) {
                        Object[] objArray = (Object[])param;
                        for(Object obj:objArray){
                            fillObjects.add(obj);
                        }
                    }else{
                        fillObjects.add(param);
                    }
                }
            }else{
                fillObjects.add(entity);
            }
            for(Object fillObject:fillObjects){
                if(sqlCommandType==SqlCommandType.INSERT){
                    fillFieldValue(insertInfos,fillObject,SqlCommandType.INSERT);
                }else if(sqlCommandType==SqlCommandType.UPDATE){
                    fillFieldValue(updateInfos,fillObject,SqlCommandType.UPDATE);
                }
            }
        }catch (Exception e){
            log.error("do AutoinjectIntegrceptor error",e);
        }
        return invocation.proceed();
    }

    /**
     * 填充属性字段
     * @param fillInfos
     * @param entity
     */
    private void fillFieldValue(List<List<Object>> fillInfos,Object entity,SqlCommandType sqlCommandType){
        MetaObject metaObject = SystemMetaObject.forObject(entity);
        for(List<Object> fillInfo:fillInfos){
            String fileldName = (String)fillInfo.get(0);
            Class fieldType = (Class)fillInfo.get(1);
            Supplier defaultValueSupplier = (Supplier)fillInfo.get(2);
            Object defaultValue = defaultValueSupplier.get();
            if(defaultValue==null||(defaultValue instanceof String && !StringUtils.hasText((String)defaultValue))){
                continue;
            }
            if(metaObject.hasSetter(fileldName)){
                Object rawValue = metaObject.getValue(fileldName);
                Class clazz = metaObject.getSetterType(fileldName);
                //新增情况下，且原来有值不处理
                if(sqlCommandType==SqlCommandType.INSERT&&rawValue != null){
                    continue;
                }
                if(fieldType.isAssignableFrom(clazz)){
                    metaObject.setValue(fileldName,defaultValue);
                }
            }
        }
    }

}
