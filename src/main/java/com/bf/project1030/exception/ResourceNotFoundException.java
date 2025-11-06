package com.bf.project1030.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 当资源不存在或被过滤掉时（如商品下架/无库存），抛出此异常。
 * GlobalExceptionHandler 会统一转为 404 返回。
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
