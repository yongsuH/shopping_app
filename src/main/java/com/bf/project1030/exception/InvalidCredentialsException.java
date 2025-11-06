package com.bf.project1030.exception;

public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException(String message) {
    super(message);
  }

  public InvalidCredentialsException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidCredentialsException() {
    super("Invalid credentials. Please try again.");
  }
}
