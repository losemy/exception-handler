package com.github.losemy.exceptionhandler.aop;

import cn.hutool.core.util.ReflectUtil;
import com.github.losemy.exceptionhandler.handler.BizHandlerResolver;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;

/**
 * @author lose
 * @date 2020-02-20
 **/
@Slf4j
@Aspect
public class BizAop {

    private BizHandlerResolver bizHandlerResolver;

    public BizAop(BizHandlerResolver resolver) {
        this.bizHandlerResolver = resolver;
    }

    /**
     * 只拦截 public方法 返回值为ResultVO 的类
     * 其他返回值类型不加以处理（其实可以加以处理，就是在查找方法的时候加上返回值查询）
     * 理论上考虑进来只会加重设计，且本身体系会有一定的混乱
     */
    @Pointcut("@within(com.github.losemy.exceptionhandler.handler.Biz)")
    public void pointcut() { }

    /**
     * 异常处理
     * @param joinPoint
     * @throws Throwable
     */
    @Around("pointcut()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        try{
            return joinPoint.proceed();
        }catch (Exception e){
            return resolverException(e);
        }
    }

    private Object resolverException(Exception e) {
        //这里不应该发生异常，如果出现异常则表示程序无法处理
        //这里method的提取最好包括返回值 目前设计不够通用，且需要额外编写
        Method method = bizHandlerResolver.getHandlerMethodResolver().resolveMethod(e);
        return ReflectUtil.invoke(bizHandlerResolver.getServiceAdvice(), method, e);
    }
}
