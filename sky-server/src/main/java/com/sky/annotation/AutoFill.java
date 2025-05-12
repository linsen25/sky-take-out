package com.sky.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.sky.enumeration.OperationType;
/**
 * 白定义注解，用于标识某个方法需要进行功能字段自动填充处理
 * 
 */
@Target(ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface AutoFill {
    // 数据库操作的枚举类型OperationType Update Insert
    OperationType value();
    
}
