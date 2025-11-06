package com.bf.project1030.exception;

public class UnauthorizedOrderAccessException extends RuntimeException {
  public UnauthorizedOrderAccessException(String message) {
    super(message);
  }
}