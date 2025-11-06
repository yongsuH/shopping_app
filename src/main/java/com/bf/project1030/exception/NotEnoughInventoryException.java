package com.bf.project1030.exception;

public class NotEnoughInventoryException extends RuntimeException {
  public NotEnoughInventoryException(String message) {
    super(message);
  }
}
