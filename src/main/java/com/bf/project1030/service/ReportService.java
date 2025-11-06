package com.bf.project1030.service;

import com.bf.project1030.DTO.ProductProfitDTO;
import com.bf.project1030.DTO.ProductStatDTO;
import com.bf.project1030.repository.ReportDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

  private final ReportDao reportDao;

  public ReportService(ReportDao reportDao) {
    this.reportDao = reportDao;
  }

  public List<ProductStatDTO> topFrequent(String username, int topN) {
    return reportDao.topFrequentProducts(username, topN);
  }

  public List<ProductStatDTO> topRecent(String username, int topN) {
    return reportDao.topRecentProducts(username, topN);
  }

  public List<ProductStatDTO> topPopular(int topN) {
    return reportDao.topPopularProducts(topN);
  }

  public List<ProductProfitDTO> topProfit(int topN) {
    return reportDao.topProfitableProducts(topN);
  }

}
