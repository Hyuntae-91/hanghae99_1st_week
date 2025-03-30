package io.hhplus.tdd;

import io.hhplus.tdd.exceptions.NotEnoughPointException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(NotEnoughPointException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughPoint(NotEnoughPointException ex) {
        return buildErrorResponse("400", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return buildErrorResponse("400", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String code, String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(code, message);
        return new ResponseEntity<>(errorResponse, status);
    }
}
