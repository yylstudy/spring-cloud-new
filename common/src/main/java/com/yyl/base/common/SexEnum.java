package com.yyl.base.common;

import com.yyl.base.validation.StringListValuable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023/5/26 15:57
 */

public enum SexEnum implements StringListValuable {
    MAN("1","男"),
    WOMAN("2","女"),
    ;
    private static final List<String> list = Arrays.stream(values()).map(SexEnum::getValue).collect(Collectors.toList());

    private String value;
    private String name;

    SexEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public List<String> list() {
        return list;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
