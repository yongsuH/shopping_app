// src/main/java/com/bf/project1030/service/AdminStatsService.java
package com.bf.project1030.service;

import com.bf.project1030.DTO.*;
import com.bf.project1030.repository.StatsDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

  private final StatsDao statsDao;

  public StatsOverviewDTO overview() {
    return statsDao.overview();
  }

  public List<RevenueDailyPoint> revenueDaily(LocalDate from, LocalDate to) {
    return statsDao.revenueDaily(from, to);
  }

  public List<TopProductDTO> topProducts(int limit, String by) {
    boolean byRevenue = "revenue".equalsIgnoreCase(by);
    return statsDao.topProducts(limit, byRevenue);
  }
}
