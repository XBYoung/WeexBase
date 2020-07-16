package com.young.weexbase.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 打印
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MethodLog {

    /**
     * 需要打印入参
     * @return
     */
    boolean needPrintArgs() default true;
    /**
     * 需要打印返回值
     * @return
     */
    boolean needReturnT() default true;
    /**
     * 方法的描述
     * @return
     */
    String methodDesc() default "";

    /**
     * 参数的描述（数组）
     * @return
     */
    String[] paramsDesc() default {};

    /**
     * 返回值的描述
     * @return
     */
    String returnDesc() default "返回值";
}
