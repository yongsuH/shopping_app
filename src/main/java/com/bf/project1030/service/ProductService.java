// src/main/java/com/example/shop/service/ProductService.java
package com.bf.project1030.service;

import com.bf.project1030.repository.ProductUserDao;
import com.bf.project1030.DTO.ProductUserDTO;
import com.bf.project1030.entity.Product;
import com.bf.project1030.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductUserDao productUserDao;

  public List<ProductUserDTO> getAllForUser() {
    return productUserDao.findAllInStockForUser().stream()
        .map(ProductService::toUserDTO).toList();
  }

  public ProductUserDTO getOneForUser(Long id) {
    Product p = productUserDao.findByIdForUserActive(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found or not active"));
    return toUserDTO(p);
  }

  private static ProductUserDTO toUserDTO(Product p) {
    return new ProductUserDTO(p.getId(), p.getName(), p.getDescription(), p.getRetailPrice());
  }
}
