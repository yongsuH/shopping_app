// src/main/java/com/bf/project1030/DTO/RevenueDailyPoint.java
package com.bf.project1030.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RevenueDailyPoint(
    LocalDate date,
    BigDecimal revenue
) {}
