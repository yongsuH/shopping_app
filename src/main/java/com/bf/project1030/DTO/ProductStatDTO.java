package com.bf.project1030.DTO;

import java.time.Instant;

// DTO for most popular report
public record ProductStatDTO(
    Long productId,
    String name,
    long totalQty,           // frequent 用
    Instant lastPurchasedAt  // recent 用
) {}
