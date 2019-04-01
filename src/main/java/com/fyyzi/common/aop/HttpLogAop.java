package com.fyyzi.common.aop;

import com.fyyzi.service.HttpControllerLogService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * http调用日志记录切面
 *
 * @author 息阳
 */
@Aspect
@Component
@Slf4j
public class HttpLogAop {

    @Autowired
    private HttpControllerLogService httpControllerLogService;

    /** 切入点表达式 */
    @Pointcut("execution(public * com.fyyzi.web.*.*(..) )")
    public void httpLogPoint() {
        // 这是一个切入点表达式的声明类,所以里面什么都不做
    }

    /**
     * 前置通知
     *
     * @param joinPoint {@link JoinPoint}
     */
    @Before("httpLogPoint()")
    public void before(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String s = objectToJson(args);
    }

    /**
     * 后置通知
     *
     * @param result 方法返回值
     */
    @AfterReturning(value = "httpLogPoint()", returning = "result")
    public void after(Object result) {
        String s = objectToJson(result);
    }

    /**
     * 环绕通知
     *
     * @param joinPoint {@link ProceedingJoinPoint}
     * @return 环绕通知必须要讲结果返回
     * @throws Throwable 被切入的方法发生的异常
     */
    @Around("httpLogPoint()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable throwable) {
            log.error("发生异常:{}", throwable);
            throw throwable;
        } finally {
            String requsetJson = objectToJson(args);
            String responseJson = objectToJson((result));
            httpControllerLogService.save(requsetJson, responseJson);
        }
    }

    /**
     * 讲对象序列化的方法
     *
     * @param obj {@link Object}
     * @return Json
     */
    private String objectToJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }
}
