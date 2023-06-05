package com.yyl.base.validation;

import java.util.List;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description 枚举类型可允许的值
 * @createTime 2023/5/26 15:54
 */

public interface StringListValuable {
    /**
     * 可允许的方法值
     * @return
     */
    List<String> list();
}
