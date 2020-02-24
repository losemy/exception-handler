package com.github.losemy.exceptionhandler.autoconfiguration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author lose
 * @date 2020-02-20
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ExceptionHandlerConfig.class)
@Documented
public @interface EnableBizExceptionHandler {

}
