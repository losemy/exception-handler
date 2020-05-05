package com.github.losemy.exceptionhandler.autoconfiguration;

import com.github.losemy.exceptionhandler.aop.BizAop;
import com.github.losemy.exceptionhandler.handler.Biz;
import com.github.losemy.exceptionhandler.handler.BizAdvice;
import com.github.losemy.exceptionhandler.handler.BizHandler;
import com.github.losemy.exceptionhandler.handler.BizHandlerResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * test1 注释
 * @author lose
 * @date 2020-02-20
 **/
@Configuration
@ConditionalOnClass({ BizAdvice.class, BizHandler.class, Biz.class})
@Slf4j
public class ExceptionHandlerConfig {

    /**
     * test1 注释
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public BizHandlerResolver exceptionHandlerResolver(){
        return new BizHandlerResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public BizAop bizAop(BizHandlerResolver resolver){
        return new BizAop(resolver);
    }


}
