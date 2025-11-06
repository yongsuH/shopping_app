package com.bf.project1030.exception;

public class InvalidOrderStatusTransitionException extends RuntimeException {
  public InvalidOrderStatusTransitionException(String message) {
    super(message);
  }
}
