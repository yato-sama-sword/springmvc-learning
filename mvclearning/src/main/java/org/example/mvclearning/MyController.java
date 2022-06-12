package org.example.mvclearning;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义一个controller注解
 * @author yato
 */
@Target(ElementType.TYPE) // 表示注解作用在类上
@Retention(RetentionPolicy.RUNTIME) // 表示注解在运行期生效
public @interface MyController {
    String value() default "";
}
