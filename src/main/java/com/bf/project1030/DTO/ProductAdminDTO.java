// src/main/java/com/bf/project1030/DTO/ProductAdminDTO.java
package com.bf.project1030.DTO;

import java.math.BigDecimal;

public record ProductAdminDTO(
    Long id,
    String name,
    String description,
    BigDecimal wholesalePrice,
    BigDecimal retailPrice,
    Integer quantity,
    Boolean active
) {}
