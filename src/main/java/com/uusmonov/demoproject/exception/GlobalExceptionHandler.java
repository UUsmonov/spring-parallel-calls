package com.uusmonov.challenge.exception;

import com.uusmonov.challenge.model.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR = "Classname: {}, Method: {}, URI {}, Message: {}";
    private static final String UNKNOWN_CLASS = "Unknown Class";
    private static final String UNKNOWN_METHOD = "Unknown Method";
    private static final String REQUEST_IS_NULL = "Request is null";
    private static final String WENT_WRONG = "Something went wrong";


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request, HandlerMethod handlerMethod) {
        logError(request, handlerMethod, ex);
        return ResponseEntity.badRequest().body(new ApiErrorResponse(ex.getLocalizedMessage()) {
        });
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex, HttpServletRequest request, HandlerMethod handlerMethod) {
        logError(request, handlerMethod, ex);
        return ResponseEntity.internalServerError().body(new ApiErrorResponse(WENT_WRONG));
    }


    private String exctractClassName(HandlerMethod handlerMethod) {
        return handlerMethod != null ? handlerMethod.getBeanType().getSimpleName() : UNKNOWN_CLASS;
    }

    private String exctractMethodName(HandlerMethod handlerMethod) {
        return handlerMethod != null ? handlerMethod.getMethod().getName() : UNKNOWN_METHOD;
    }

    private String exctractUri(HttpServletRequest request) {
        return request != null ? request.getRequestURI() : REQUEST_IS_NULL;
    }

    private void logError(HttpServletRequest request, HandlerMethod handlerMethod, Exception ex) {
        log.error(ERROR, exctractClassName(handlerMethod), exctractMethodName(handlerMethod), exctractUri(request), ex.getMessage(), ex);
    }
}
