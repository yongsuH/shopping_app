package com.bf.project1030.DTO;

import java.time.Instant;

// 通用统计 DTO：频次接口用 totalQty，最近接口用 lastPurchasedAt
public record ProductStatDTO(
    Long productId,
    String name,
    long totalQty,           // frequent 用
    Instant lastPurchasedAt  // recent 用
) {}
