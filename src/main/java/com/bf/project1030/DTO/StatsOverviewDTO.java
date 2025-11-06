// src/main/java/com/bf/project1030/DTO/StatsOverviewDTO.java
package com.bf.project1030.DTO;

import java.math.BigDecimal;

public record StatsOverviewDTO(
    long ordersCount,
    long itemsSold,
    BigDecimal totalRevenue
) {}
