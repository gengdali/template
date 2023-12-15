package com.huizi.easydinner.anotation;

import java.lang.annotation.*;

/**
 * @PROJECT_NAME: personal
 * @DESCRIPTION:Excel表头注解
 * @AUTHOR: 12615
 * @DATE: 2023/5/17 17:00
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelHeader {

    /**
     * 表头
     *
     * @return
     */
    String value() default "";

    /**
     * 列索引
     *
     * @return
     */
    int columnIndex() default 0;

}