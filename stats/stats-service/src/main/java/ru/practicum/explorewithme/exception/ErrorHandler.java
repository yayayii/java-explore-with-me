package ru.practicum.explorewithme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException e) {
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request", e);
        log.warn(errorResponse.toString());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request", e);
        log.warn(errorResponse.toString());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(final Exception e) {
        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error: " + e.getClass().getName(), e
        );
        log.warn(errorResponse.toString());
        return ResponseEntity.internalServerError().body(errorResponse);
    }


    private ErrorResponse createErrorResponse(HttpStatus statusCode, String reason, Exception e) {
        return new ErrorResponse(statusCode.getReasonPhrase(), reason, e.getMessage(), LocalDateTime.now());
    }
}
