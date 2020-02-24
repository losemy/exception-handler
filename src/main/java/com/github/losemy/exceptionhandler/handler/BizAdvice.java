package com.github.losemy.exceptionhandler.handler;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author lose
 * @date 2019-12-02
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface BizAdvice {

}
