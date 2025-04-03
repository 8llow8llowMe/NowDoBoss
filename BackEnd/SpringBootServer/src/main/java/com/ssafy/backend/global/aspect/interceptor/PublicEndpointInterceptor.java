package com.ssafy.backend.global.aspect.interceptor;

import com.ssafy.backend.global.annotation.PublicEndpoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class PublicEndpointInterceptor implements HandlerInterceptor {

    private static final String ATTRIBUTE_NAME = "IS_PUBLIC";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        boolean isPublic = handlerMethod.getMethodAnnotation(PublicEndpoint.class) != null
            || handlerMethod.getBeanType().isAnnotationPresent(PublicEndpoint.class);

        if (isPublic) {
            request.setAttribute(ATTRIBUTE_NAME, true);
        }

        return true;
    }
}
