package ru.practicum.explorewithme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(final ConstraintViolationException e) {
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request", e);
        log.warn(errorResponse.toString());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException e) {
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request", e);
        log.warn(errorResponse.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request", e);
        log.warn(errorResponse.toString());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
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
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(final NoSuchElementException e) {
        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.NOT_FOUND, "The required object was not found", e
        );
        log.warn(errorResponse.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.CONFLICT, "Integrity constraint has been violated", e
        );
        log.warn(errorResponse.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
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
