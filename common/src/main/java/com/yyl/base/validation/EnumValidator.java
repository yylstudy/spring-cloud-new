package com.yyl.base.validation;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description ConstraintValidator 是线程安全的，所以可以将注解作为全局变量
 * @createTime 2023/5/26 15:51
 */

public class EnumValidator implements ConstraintValidator<Enum, String> {
    /**
     * 允许的枚举值
     */
    private List<String> allowValue = new ArrayList<>();

    @Override
    public void initialize(Enum constraintAnnotation) {
        StringListValuable[] enums = constraintAnnotation.value().getEnumConstants();
        if(enums.length>0){
            allowValue = enums[0].list();
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(!StringUtils.hasLength(value)){
            return true;
        }
        if(allowValue.contains(value)){
            return true;
        }
        //禁用默认的message值
        context.disableDefaultConstraintViolation();
        //添加新的message提示
        String allValueStr = allowValue.stream().collect(Collectors.joining(","));
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()
                .replaceAll("\\{value}", allValueStr)).addConstraintViolation();
        return false;
    }
}
