package com.upc.webmarketar.infrastructure;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final String ID = RequestLoggingFilter.class.getName() + ".id";
    private static final String START = RequestLoggingFilter.class.getName() + ".start";
    private static final String ROUTE = RequestLoggingFilter.class.getName() + ".route";

    @Override
    protected boolean shouldNotFilterErrorDispatch() { return false; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        // Always generate a UUID; do not reuse caller-supplied IDs.
        boolean first = request.getAttribute(ID) == null;
        String requestId = first ? UUID.randomUUID().toString() : (String) request.getAttribute(ID);
        if (first) {
            request.setAttribute(ID, requestId);
            request.setAttribute(START, System.nanoTime());
            request.setAttribute(ROUTE, RequestLog.safePath(request.getRequestURI()));
        }
        MDC.put("requestId", requestId);
        response.setHeader("X-Request-ID", requestId);
        long started = (long) request.getAttribute(START);
        String method = request.getMethod().matches("GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS") ? request.getMethod() : "OTHER";
        String route = (String) request.getAttribute(ROUTE);
        boolean escaped = false;
        try {
            if (first) log.info("event=request_received method={} route={} operation=unresolved requestId={}", method, route, requestId);
            chain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException exception) {
            escaped = true;
            RequestLog.unexpected(request, exception);
            throw exception;
        } finally {
            try {
                long duration = (System.nanoTime() - started) / 1_000_000;
                // Unhandled exceptions finish in the container's ERROR dispatch; do not invent a status.
                if (!escaped || response.isCommitted()) {
                    int status = response.getStatus();
                    String operation = RequestLog.operation(request);
                    if (operation.equals("unresolved")) {
                        operation = status == 401 ? "authentication_rejected"
                                : status == 403 ? "authorization_rejected" : "http_request";
                    }
                    if (status >= 400 && (status < 500 || Boolean.TRUE.equals(request.getAttribute(RequestLog.EXPECTED_ERROR))))
                        log.warn("event=request_completed method={} route={} operation={} requestId={} status={} durationMs={}", method, route, operation, requestId, status, duration);
                    else if (status >= 500 && !Boolean.TRUE.equals(request.getAttribute(RequestLog.ERROR_LOGGED)))
                        log.error("event=request_completed method={} route={} operation={} requestId={} status={} durationMs={}", method, route, operation, requestId, status, duration);
                    else
                        log.info("event=request_completed method={} route={} operation={} requestId={} status={} durationMs={}", method, route, operation, requestId, status, duration);
                }
            } finally {
                MDC.remove("requestId");
            }
        }
    }

}
