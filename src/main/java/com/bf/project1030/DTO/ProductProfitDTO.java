package com.bf.project1030.DTO;

import java.math.BigDecimal;

public record ProductProfitDTO(
    Long productId,
    String name,
    BigDecimal profit
) {}