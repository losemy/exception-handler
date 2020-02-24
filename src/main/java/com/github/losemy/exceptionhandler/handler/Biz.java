package com.github.losemy.exceptionhandler.handler;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author lose
 * @date 2020-02-20
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Biz {

    @AliasFor(annotation = Component.class)
    String value() default "";
}
