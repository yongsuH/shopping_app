package com.bf.project1030.service;

import com.bf.project1030.DTO.ProductStatDTO;
import com.bf.project1030.repository.UserStatDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserStatService {

  private final UserStatDao reportDao;

  public UserStatService(UserStatDao reportDao) {
    this.reportDao = reportDao;
  }

  public List<ProductStatDTO> topFrequent(String username, int topN) {
    return reportDao.topFrequentProducts(username, topN);
  }

  public List<ProductStatDTO> topRecent(String username, int topN) {
    return reportDao.topRecentProducts(username, topN);
  }
}
