package com.shy.mydogcode.annotation;

import java.lang.annotation.*;

/**
 * WebServlet 注解
 * */
@Documented//java doc收录
@Target(ElementType.TYPE)//标注在类上
@Retention(RetentionPolicy.RUNTIME)//运行期生效
public @interface WebServlet {
    String[] value();//urlPatterns
}
