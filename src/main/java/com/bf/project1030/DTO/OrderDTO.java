// src/main/java/com/example/shop/dto/OrderDTO.java
package com.bf.project1030.DTO;

import java.time.Instant;
import java.util.List;

public record OrderDTO(
    Long id,
    Double totalPrice,
    Instant createdAt,
    Long userId,
    String username,
    List<OrderItemDTO> items
) {}
