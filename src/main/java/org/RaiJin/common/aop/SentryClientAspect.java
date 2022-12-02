package org.RaiJin.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.RaiJin.common.env.EnvConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
@Slf4j
public class SentryClientAspect {

    @Autowired
    EnvConfig envConfig;

    @Around("execution(* io.sentry.SentryClient.send*(..))")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable{
        if(envConfig.isDebug()) {
            log.debug("no sentry logging in debug mode");
            return;
        }
        joinPoint.proceed();
    }
}
