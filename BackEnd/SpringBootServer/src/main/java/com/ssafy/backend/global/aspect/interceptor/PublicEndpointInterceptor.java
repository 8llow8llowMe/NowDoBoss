package com.ssafy.backend.global.aspect.interceptor;

import com.ssafy.backend.global.annotation.PublicEndpoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class PublicEndpointInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            if (handlerMethod.getMethodAnnotation(PublicEndpoint.class) != null ||
                handlerMethod.getBeanType().getAnnotation(PublicEndpoint.class) != null) {
                request.setAttribute("IS_PUBLIC", true);
            }
        }
        return true;
    }
}
