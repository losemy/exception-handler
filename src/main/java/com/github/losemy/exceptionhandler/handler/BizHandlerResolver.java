package com.github.losemy.exceptionhandler.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @author lose
 * @date 2019-12-02
 **/
@Slf4j
public class BizHandlerResolver implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private BizHandlerMethodResolver handlerMethodResolver;

    private Object serviceAdvice;

    @Override
    public void afterPropertiesSet() throws Exception {
        initExceptionMethodHandler();
    }

    private void initExceptionMethodHandler() {
        if (getApplicationContext() == null) {
            return;
        }

        Map<String, Object> serviceAdviceBeansMap = getApplicationContext().getBeansWithAnnotation(BizAdvice.class);
        //正常只有一个，如果有多个需要找到

        if(serviceAdviceBeansMap == null || serviceAdviceBeansMap.size() > 1){
            throw new RuntimeException("没有或者有多个ServiceAdvice");
        }

        for(Map.Entry<String,Object> entry : serviceAdviceBeansMap.entrySet()){
            Object serviceAdvice = entry.getValue();
            this.serviceAdvice = serviceAdvice;
            this.handlerMethodResolver = new BizHandlerMethodResolver(serviceAdvice.getClass());
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }


    public BizHandlerMethodResolver getHandlerMethodResolver() {
        return this.handlerMethodResolver;
    }

    public Object getServiceAdvice() {
        return this.serviceAdvice;
    }
}
