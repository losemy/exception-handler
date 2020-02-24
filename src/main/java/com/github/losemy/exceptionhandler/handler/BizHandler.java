package com.github.losemy.exceptionhandler.handler;

import java.lang.annotation.*;

/**
 * @author lose
 * @date 2019-12-02
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizHandler {

    /**
     * Exceptions handled by the annotated method. If empty, will default to any
     * exceptions listed in the method argument list.
     */
    Class<? extends Throwable>[] value() default {};

}
