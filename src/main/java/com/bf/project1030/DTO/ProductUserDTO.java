// src/main/java/com/example/shop/dto/ProductUserDTO.java
package com.bf.project1030.DTO;

import java.math.BigDecimal;

public record ProductUserDTO(
    Long id,
    String name,
    String description,
    BigDecimal retailPrice
) {}
