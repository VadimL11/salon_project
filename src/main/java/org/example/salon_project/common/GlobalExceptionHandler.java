package org.example.salon_project.common;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.example.salon_project.exception.ConflictException;
import org.example.salon_project.exception.NotFoundException;
import org.example.salon_project.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(NotFoundException.class)
        public ApiError handleNotFound(NotFoundException ex) {
                log.warn("Not found: {}", ex.getMessage());
                return ApiError.builder()
                                .code("NOT_FOUND")
                                .message(ex.getMessage())
                                .build();
        }

        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(ResourceNotFoundException.class)
        public ApiError handleResourceNotFound(ResourceNotFoundException ex) {
                log.warn("Resource not found: {}", ex.getMessage());
                return ApiError.builder()
                                .code("NOT_FOUND")
                                .message(ex.getMessage())
                                .build();
        }

        @ResponseStatus(HttpStatus.CONFLICT)
        @ExceptionHandler(ConflictException.class)
        public ApiError handleConflict(ConflictException ex) {
                log.warn("Conflict: {}", ex.getMessage());
                return ApiError.builder()
                                .code("CONFLICT")
                                .message(ex.getMessage())
                                .build();
        }

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ApiError handleValidation(MethodArgumentNotValidException ex) {
                List<ApiError.FieldError> details = ex.getBindingResult().getFieldErrors().stream()
                                .map(fe -> ApiError.FieldError.builder()
                                                .field(fe.getField())
                                                .issue(fe.getDefaultMessage())
                                                .build())
                                .toList();

                return ApiError.builder()
                                .code("VALIDATION_ERROR")
                                .message("Invalid request")
                                .details(details)
                                .build();
        }

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(ConstraintViolationException.class)
        public ApiError handleConstraintViolation(ConstraintViolationException ex) {
                List<ApiError.FieldError> details = ex.getConstraintViolations().stream()
                                .map(cv -> ApiError.FieldError.builder()
                                                .field(cv.getPropertyPath().toString())
                                                .issue(cv.getMessage())
                                                .build())
                                .toList();

                return ApiError.builder()
                                .code("VALIDATION_ERROR")
                                .message("Invalid request")
                                .details(details)
                                .build();
        }

        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        @ExceptionHandler(Exception.class)
        public ApiError handleGeneral(Exception ex) {
                log.error("Unexpected error", ex);
                return ApiError.builder()
                                .code("INTERNAL_ERROR")
                                .message("An unexpected error occurred")
                                .build();
        }
}
