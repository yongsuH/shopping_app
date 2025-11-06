// src/main/java/com/bf/project1030/DTO/TopProductDTO.java
package com.bf.project1030.DTO;

import java.math.BigDecimal;

public record TopProductDTO(
    Long productId,
    String name,
    long quantitySold,
    BigDecimal revenue
) {}
