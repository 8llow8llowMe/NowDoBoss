package com.ssafy.backend.global.aspect.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class ApiAccessLoggingAspect {

    @After("within(@org.springframework.web.bind.annotation.RestController *)")
    public void logAllApiRequests(JoinPoint joinPoint) {
        HttpServletRequest request =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String method = request.getMethod();

        String controllerName = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("[API ACCESS] [{}] URI: {}, IP: {}, Controller: {}, Method: {}",
            method, uri, ip, controllerName, methodName);
    }
}
