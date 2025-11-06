package com.bf.project1030.controller;

import com.bf.project1030.DTO.ProductAdminDTO;
import com.bf.project1030.DTO.ProductAdminUpsertReq;
import com.bf.project1030.DTO.ProductUserDTO;
import com.bf.project1030.service.AdminProductService;
import com.bf.project1030.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;
  private final AdminProductService adminProductService;

  // ===== 同一路径：GET /products/all —— token区分返回 =====
  @GetMapping("/all")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<?> all(Authentication authentication,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    if (hasRole(authentication, "ROLE_ADMIN")) {
      // 管理员视图（分页）
      List<ProductAdminDTO> data = adminProductService.list(page, size);
      return ResponseEntity.ok(data);
    } else {
      // 用户视图（你原来是一次性全量，如果也要分页可同步改 service）
      List<ProductUserDTO> data = productService.getAllForUser();
      return ResponseEntity.ok(data);
    }
  }

  // ===== 同一路径：GET /products/{id} —— token区分返回 =====
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<?> getOne(Authentication authentication, @PathVariable Long id) {
    if (hasRole(authentication, "ROLE_ADMIN")) {
      ProductAdminDTO data = adminProductService.get(id);
      return ResponseEntity.ok(data);
    } else {
      ProductUserDTO data = productService.getOneForUser(id);
      return ResponseEntity.ok(data);
    }
  }

  // ===== 同一路径（仅管理员可用）：POST /products —— 新增 =====
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductAdminDTO> create(@Valid @RequestBody ProductAdminUpsertReq req) {
    return ResponseEntity.ok(adminProductService.create(req));
  }

  // ===== 同一路径（仅管理员可用）：PUT /products/{id} —— 全量修改 =====
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductAdminDTO> update(@PathVariable Long id,
      @Valid @RequestBody ProductAdminUpsertReq req) {
    return ResponseEntity.ok(adminProductService.update(id, req));
  }

  // ===== 同一路径（仅管理员可用）：PATCH /products/{id}/active —— 上/下架 =====
  @PatchMapping("/{id}/active")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductAdminDTO> toggleActive(@PathVariable Long id,
      @RequestParam boolean value) {
    return ResponseEntity.ok(adminProductService.toggleActive(id, value));
  }

  // ===== 工具方法：判断是否具有某角色 =====
  public static boolean hasRole(Authentication auth, String role) {
    if (auth == null) return false;
    Collection<?> authorities = auth.getAuthorities();
    if (authorities == null) return false;
    return authorities.stream().anyMatch(a -> role.equals(a.toString()));
  }
}
