package com.ecommerce.order_service.exception;

import com.ecommerce.order_service.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle OrderNotFoundException - HTTP 404 Not Found
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(
            OrderNotFoundException ex,
            HttpServletRequest request) {
        
        logger.warn("OrderNotFoundException caught - Message: {}, Path: {}", ex.getMessage(), request.getRequestURI());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "Order Not Found",
                request.getRequestURI(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle PaymentFailedException - HTTP 402 Payment Required
     */
    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentFailedException(
            PaymentFailedException ex,
            HttpServletRequest request) {
        
        logger.error("PaymentFailedException caught - Message: {}, Path: {}", ex.getMessage(), request.getRequestURI());
        
        HttpStatus status = HttpStatus.PAYMENT_REQUIRED;
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "Payment Failed",
                request.getRequestURI(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Handle MethodArgumentNotValidException - HTTP 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        logger.warn("Validation exception caught - Path: {}", request.getRequestURI());
        
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("timestamp", LocalDateTime.now());
        error.put("message", "Validation failed");
        error.put("path", request.getRequestURI());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            if (err instanceof FieldError) {
                FieldError field = (FieldError) err;
                fieldErrors.put(field.getField(), field.getDefaultMessage());
                logger.debug("Field validation error - Field: {}, Message: {}", field.getField(), field.getDefaultMessage());
            } else {
                fieldErrors.put(err.getObjectName(), err.getDefaultMessage());
                logger.debug("Object validation error - Object: {}, Message: {}", err.getObjectName(), err.getDefaultMessage());
            }
        });
        
        error.put("errors", fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle JWT and Authentication Exceptions - HTTP 401 Unauthorized
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {
        
        logger.warn("AuthenticationException caught - Message: {}, Path: {}", ex.getMessage(), request.getRequestURI());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication failed: " + ex.getMessage(),
                "Unauthorized",
                request.getRequestURI(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handle BadCredentialsException - HTTP 401 Unauthorized
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request) {
        
        logger.warn("BadCredentialsException caught - Path: {}", request.getRequestURI());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid credentials",
                "Unauthorized",
                request.getRequestURI(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handle IllegalArgumentException - HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        
        logger.warn("IllegalArgumentException caught - Message: {}, Path: {}", ex.getMessage(), request.getRequestURI());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Invalid Argument",
                request.getRequestURI(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle all other exceptions - HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {
        
        logger.error("Unexpected exception caught - Type: {}, Message: {}, Path: {}", 
            ex.getClass().getSimpleName(), ex.getMessage(), request.getRequestURI(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please try again later.",
                ex.getClass().getSimpleName(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

