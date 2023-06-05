package com.yyl.base.validation;

import org.springframework.util.StringUtils;
import org.springframework.validation.ValidationUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description 手机格式校验器
 * @createTime 2023/5/26 15:47
 */

public class MobileValidator implements ConstraintValidator<Mobile, String> {
    private String[] regex;
    @Override
    public void initialize(Mobile mobile) {
        regex = mobile.regexp();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (!StringUtils.hasLength(value)) {
            return true;
        }
        return Arrays.stream(regex).anyMatch(regex-> Pattern.compile(regex).matcher(value).matches());
    }


    public static void main(String[] args) {
        boolean ss = Pattern.compile("[^private_bind_info_axe_[0-9]*_[0-9]*]").matcher("private_bind_info_axe_1_1").matches();
        System.out.println(ss);
    }
}
