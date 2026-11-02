package com.upc.webmarketar.infrastructure;

import org.slf4j.MDC;

/** Capture before enqueueing; restore worker context even when the task fails. */
public final class RequestIdTask {
    private RequestIdTask() {}
    public static Runnable wrap(Runnable task) {
        String captured = MDC.get("requestId");
        return () -> {
            String previous = MDC.get("requestId");
            try {
                if (captured == null) MDC.remove("requestId"); else MDC.put("requestId", captured);
                task.run();
            } finally {
                if (previous == null) MDC.remove("requestId"); else MDC.put("requestId", previous);
            }
        };
    }
}
