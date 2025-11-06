package com.bf.project1030.DTO;

import java.math.BigDecimal;

// PATCH request
public record ProductAdminPatchReq(
    String name,
    String description,
    BigDecimal wholesalePrice,
    BigDecimal retailPrice,
    Integer quantity
) {}
