package com.Support.SupportTeam.ExceptionHandler;

import com.Support.SupportTeam.CustomExceptions.*;
import com.Support.SupportTeam.Response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

    // Handle validation errors (e.g., missing fields in requests)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        System.out.println("i am here");
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    // Handle a project not found exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse response = new ApiResponse(ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 Not Found
    }

    // Handle a Deleted Users exceptions
    @ExceptionHandler(UserDeletedException.class)
    public ResponseEntity<ApiResponse> handleUserAlreadyDeletedException(UserDeletedException ex) {
        ApiResponse response = new ApiResponse(ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 404 Not Found
    }

    // Handle cases where a project with the same name already exists
    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<ApiResponse> handleResourceAlreadyExistException(ResourceAlreadyExistException ex) {
        ApiResponse response = new ApiResponse(ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 Conflict
    }

    // Handle cases where an invalid request is made (e.g., start date > end date)
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse> handleInvalidInputException(InvalidInputException ex) {
        ApiResponse response = new ApiResponse(ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 400 Bad Request
    }

    // Handle cases where no projects are found (for empty list responses)
    @ExceptionHandler(NoProjectsFoundException.class)
    public ResponseEntity<ApiResponse> handleNoProjectsFoundException(NoProjectsFoundException ex) {
        ApiResponse response = new ApiResponse(ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 204 No Content
    }

    // Handle database constraint violations (e.g., foreign key violations)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDatabaseException(DataIntegrityViolationException ex) {
        ApiResponse response = new ApiResponse("Database constraint violation: " + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 Conflict
    }

    // Handle general unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(Exception ex) {
        ApiResponse response = new ApiResponse("An unexpected error occurred: " + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error
    }



    // Handle invalid date format - MethodArgumentTypeMismatchException
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid date format. Please use the format 'yyyy-MM-dd'.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(message, null));
    }

    // Handle invalid date parsing - DateTimeParseException
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiResponse> handleDateTimeParseException(DateTimeParseException ex) {
        String message = "Invalid date format. Please use the format 'yyyy-MM-dd'.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(message, null));
    }
}
