package com.upc.webmarketar.exceptions;

public class ApiException extends RuntimeException {
    public final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public static ApiException bad(String m) {
        return new ApiException(400, m);
    }

    public static ApiException missing() {
        return new ApiException(404, "Recurso no encontrado");
    }

    public static ApiException conflict(String m) {
        return new ApiException(409, m);
    }
}
