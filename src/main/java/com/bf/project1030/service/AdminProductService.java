// src/main/java/com/bf/project1030/service/AdminProductService.java
package com.bf.project1030.service;

import com.bf.project1030.DTO.ProductAdminDTO;
import com.bf.project1030.DTO.ProductAdminUpsertReq;
import com.bf.project1030.domain.entity.Product;
import com.bf.project1030.repository.ProductUserDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProductService {

  private final ProductUserDao productUserDao;

  public List<ProductAdminDTO> list(int page, int size) {
    return productUserDao.findAllForAdmin(page, size).stream()
        .map(AdminProductService::toAdminDTO)
        .toList();
  }

  public ProductAdminDTO get(Long id) {
    return toAdminDTO(productUserDao.require(id));
  }

  @Transactional
  public ProductAdminDTO create(ProductAdminUpsertReq req) {
    Product p = Product.builder()
        .name(req.name())
        .description(req.description())
        .wholesalePrice(req.wholesalePrice())
        .retailPrice(req.retailPrice())
        .quantity(req.quantity())
        .active(req.active())
        .build();
    return toAdminDTO(productUserDao.save(p));
  }

  @Transactional
  public ProductAdminDTO update(Long id, ProductAdminUpsertReq req) {
    Product p = productUserDao.require(id);
    p.setName(req.name());
    p.setDescription(req.description());
    p.setWholesalePrice(req.wholesalePrice());
    p.setRetailPrice(req.retailPrice());
    p.setQuantity(req.quantity());
    p.setActive(req.active());
    return toAdminDTO(productUserDao.save(p));
  }

  @Transactional
  public ProductAdminDTO toggleActive(Long id, boolean active) {
    Product p = productUserDao.require(id);
    p.setActive(active);
    return toAdminDTO(productUserDao.save(p));
  }

  private static ProductAdminDTO toAdminDTO(Product p) {
    return new ProductAdminDTO(
        p.getId(), p.getName(), p.getDescription(),
        p.getWholesalePrice(), p.getRetailPrice(),
        p.getQuantity(), p.getActive()
    );
  }
}
