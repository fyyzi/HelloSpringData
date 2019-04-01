package com.fyyzi.common.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 获取被Spring代理的Bean对象(可在任意位置使用)
 *
 * @author 息阳
 */
@Component
@Lazy(false)
public class BeanApplicationContextUtil implements ApplicationContextAware {

    private BeanApplicationContextUtil() {
    }

    /** 自己 */
    private static final BeanApplicationContextUtil APPLICATION_CONTEXT_UTIL = new BeanApplicationContextUtil();

    /** 应用上下文 */
    private ApplicationContext applicationContext;

    /**
     * 获取Application对象
     *
     * @return {@link ApplicationContext}
     */
    private static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT_UTIL.applicationContext;
    }

    /**
     * 根据 {@link Class<T>} 获取被Spring代理的对象
     *
     * @param clazz {@link Class<T>}
     * @param <T>   泛型(被Spring代理的Bean)
     * @return {@link T}
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String beanName,Class<T> clazz){
        return getApplicationContext().getBean(beanName,clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        APPLICATION_CONTEXT_UTIL.applicationContext = applicationContext;
    }
}
