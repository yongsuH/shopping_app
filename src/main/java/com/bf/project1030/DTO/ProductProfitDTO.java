package com.bf.project1030.DTO;

import java.math.BigDecimal;

// dto for profit report, bigdecimal for profit
public record ProductProfitDTO(
    Long productId,
    String name,
    BigDecimal profit
) {}