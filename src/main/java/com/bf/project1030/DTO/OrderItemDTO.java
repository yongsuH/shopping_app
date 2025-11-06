// src/main/java/com/example/shop/dto/OrderItemDTO.java
package com.bf.project1030.DTO;

import java.math.BigDecimal;

public record OrderItemDTO(
    Long productId,
    String productName,
    Integer quantity,
    BigDecimal price
) {}
