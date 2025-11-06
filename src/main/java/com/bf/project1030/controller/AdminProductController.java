// src/main/java/com/bf/project1030/controller/AdminProductController.java
package com.bf.project1030.controller;

import com.bf.project1030.DTO.ProductAdminDTO;
import com.bf.project1030.DTO.ProductAdminUpsertReq;
import com.bf.project1030.service.AdminProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')") // 整个控制器都需要 ADMIN
public class AdminProductController {

  private final AdminProductService adminProductService;

  // 分页列表
  @GetMapping
  public ResponseEntity<List<ProductAdminDTO>> list(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(adminProductService.list(page, size));
  }

  // 详情
  @GetMapping("/{id}")
  public ResponseEntity<ProductAdminDTO> get(@PathVariable Long id) {
    return ResponseEntity.ok(adminProductService.get(id));
  }

  // 新增
  @PostMapping
  public ResponseEntity<ProductAdminDTO> create(@Valid @RequestBody ProductAdminUpsertReq req) {
    return ResponseEntity.ok(adminProductService.create(req));
  }

  // 全量修改
  @PutMapping("/{id}")
  public ResponseEntity<ProductAdminDTO> update(@PathVariable Long id,
      @Valid @RequestBody ProductAdminUpsertReq req) {
    return ResponseEntity.ok(adminProductService.update(id, req));
  }

  // 上/下架
  @PatchMapping("/{id}/active")
  public ResponseEntity<ProductAdminDTO> toggleActive(@PathVariable Long id,
      @RequestParam boolean value) {
    return ResponseEntity.ok(adminProductService.toggleActive(id, value));
  }
}
