package org.example.springboot.common.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字典翻译
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {
    Class<? extends Enum<?>> enumClass();

    String fromMethod() default "getByCode";

    String toMethod() default "getMsg";
}
