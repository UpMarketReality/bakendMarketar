package com.upc.webmarketar.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Set;
import java.util.StringJoiner;

/** Only technical metadata: exception messages/causes may contain input or SQL. */
@Slf4j
public final class RequestLog {
    public static final String OPERATION = RequestLog.class.getName() + ".operation";
    public static final String ERROR_LOGGED = RequestLog.class.getName() + ".errorLogged";
    public static final String EXPECTED_ERROR = RequestLog.class.getName() + ".expectedError";
    private RequestLog() {}

    public static String operation(HttpServletRequest request) {
        Object operation = request.getAttribute(OPERATION);
        return operation == null ? "unresolved" : operation.toString();
    }

    public static void unexpected(HttpServletRequest request, Exception exception) {
        if (Boolean.TRUE.equals(request.getAttribute(ERROR_LOGGED))) return;
        request.setAttribute(ERROR_LOGGED, true);
        log.error("event=unexpected_error requestId={} operation={} exceptionType={}",
                MDC.get("requestId"), operation(request), exception.getClass().getName(), safeTrace(exception));
    }

    public static Throwable safeTrace(Exception exception) {
        var safe = new RuntimeException("Internal failure (message and causes omitted)");
        safe.setStackTrace(exception.getStackTrace());
        return safe;
    }

    /** Unmatched paths and invalid IDs can themselves contain personal data. */
    public static String safePath(String uri) {
        Set<String> segments = Set.of("api", "v1", "auth", "login", "register", "users", "me",
                "favorites", "products", "product-categories", "prototype-categories", "prototypes",
                "images", "cart", "items", "checkout", "purchases", "quote-requests", "offers",
                "orders", "seller", "reports", "sales", "summary", "status", "media", "error",
                "swagger-ui", "swagger-ui.html", "api-docs", "v3", "requests", "accept", "cancel", "specifications", "store");
        StringJoiner path = new StringJoiner("/");
        for (String segment : uri.split("/", -1)) {
            path.add(segments.contains(segment) || segment.matches("[0-9]{1,19}")
                    || segment.matches("[a-f0-9-]{1,40}\\.(png|jpg|webp)") ? segment : segment.isEmpty() ? "" : "{redacted}");
        }
        String result = path.toString();
        return result.length() <= 300 ? result : "/{redacted}";
    }
}
