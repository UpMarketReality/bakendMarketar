package com.upc.webmarketar.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

/** Shared controller context; do not repeat request results in every controller. */
@Component
@Slf4j
public class ControllerLoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod method
                && method.getBeanType().getPackageName().startsWith("com.upc.webmarketar")) {
            String operation = method.getBeanType().getSimpleName() + "." + method.getMethod().getName();
            request.setAttribute(RequestLog.OPERATION, operation);
            log.debug("operation={}", operation);
            Object variables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (variables instanceof Map<?, ?> ids)
                ids.forEach((key, value) -> {
                    if (key instanceof String name && name.endsWith("Id")
                            && value instanceof String id && id.matches("[0-9]{1,19}"))
                        log.debug("operation={} resource={} resourceId={}", operation, name, id);
                });
        }
        return true;
    }
}
