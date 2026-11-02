package com.upc.webmarketar.exceptions;

import com.upc.webmarketar.infrastructure.RequestLog;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ErrorResponse(
            int status, String message, Map<String, String> fields, Instant timestamp) {}

    private ResponseEntity<ErrorResponse> response(int s, String m, Map<String, String> f) {
        return ResponseEntity.status(s).body(new ErrorResponse(s, m, f, Instant.now()));
    }

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErrorResponse> api(ApiException e, HttpServletRequest request) {
        request.setAttribute(RequestLog.EXPECTED_ERROR, true);
        return response(e.status, e.getMessage(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e) {
        Map<String, String> f = new LinkedHashMap<>();
        e.getBindingResult()
                .getFieldErrors()
                .forEach(x -> f.put(x.getField(), x.getDefaultMessage()));
        return response(400, "Revisa los campos", f);
    }

    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class,
        MissingRequestHeaderException.class,
        MissingServletRequestPartException.class,
        MaxUploadSizeExceededException.class
    })
    ResponseEntity<ErrorResponse> input(Exception e) {
        return response(
                400, "Formato inválido, parámetro ausente o archivo demasiado grande", Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErrorResponse> integrity(Exception e) {
        return response(409, "La operación entra en conflicto con los datos existentes", Map.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorResponse> denied(Exception e) {
        return response(403, "No tienes permiso para esta acción", Map.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> unexpected(Exception e, HttpServletRequest request) {
        RequestLog.unexpected(request, e);
        return response(500, "No se pudo completar la operación", Map.of());
    }
}
