// src/main/java/com/bf/project1030/controller/AdminStatsController.java
package com.bf.project1030.controller;

import com.bf.project1030.DTO.*;
import com.bf.project1030.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

  private final AdminStatsService service;

  // 概览
  @GetMapping("/overview")
  public ResponseEntity<StatsOverviewDTO> overview() {
    return ResponseEntity.ok(service.overview());
  }

  // 日营收（闭区间）
  @GetMapping("/revenue/daily")
  public ResponseEntity<List<RevenueDailyPoint>> revenueDaily(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    return ResponseEntity.ok(service.revenueDaily(from, to));
  }

  // 热销商品 Top N
  @GetMapping("/top-products")
  public ResponseEntity<List<TopProductDTO>> topProducts(
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(defaultValue = "quantity") String by // quantity | revenue
  ) {
    return ResponseEntity.ok(service.topProducts(limit, by));
  }
}
