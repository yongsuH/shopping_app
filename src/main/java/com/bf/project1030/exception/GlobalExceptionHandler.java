package com.bf.project1030.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<String> handleDuplicate(DuplicateResourceException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleOther(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Error: " + ex.getMessage());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
        "error", "Not Found",
        "message", ex.getMessage()
    ));
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidCredentialsException ex) {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("message", ex.getMessage()));
  }

  @org.springframework.web.bind.annotation.ExceptionHandler(NotEnoughInventoryException.class)
  public org.springframework.http.ResponseEntity<java.util.Map<String,String>> handleNotEnoughInventory(NotEnoughInventoryException ex) {
    return org.springframework.http.ResponseEntity
        .status(org.springframework.http.HttpStatus.BAD_REQUEST)
        .body(java.util.Map.of("message", ex.getMessage()));
  }

  @org.springframework.web.bind.annotation.ExceptionHandler(InvalidOrderStatusTransitionException.class)
  public org.springframework.http.ResponseEntity<java.util.Map<String, String>> handleInvalidOrderStatus(InvalidOrderStatusTransitionException ex) {
    return org.springframework.http.ResponseEntity
        .status(org.springframework.http.HttpStatus.BAD_REQUEST)
        .body(java.util.Map.of("message", ex.getMessage()));
  }
}
