package com.bf.project1030.controller;

import com.bf.project1030.DTO.ProductAdminDTO;
import com.bf.project1030.DTO.ProductAdminPatchReq;
import com.bf.project1030.DTO.ProductAdminUpsertReq;
import com.bf.project1030.DTO.ProductProfitDTO;
import com.bf.project1030.DTO.ProductStatDTO;
import com.bf.project1030.DTO.ProductUserDTO;
import com.bf.project1030.service.AdminProductService;
import com.bf.project1030.service.ProductService;
import com.bf.project1030.service.ReportService;
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
  private final ReportService reportService;

  // One url：GET /products/all  differ by token
  @GetMapping("/all")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<?> all(Authentication authentication,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    if (hasRole(authentication, "ROLE_ADMIN")) {
      // view by pages
      List<ProductAdminDTO> data = adminProductService.list(page, size);
      return ResponseEntity.ok(data);
    } else {
      // user page all at a time
      List<ProductUserDTO> data = productService.getAllForUser();
      return ResponseEntity.ok(data);
    }
  }

  // One url：GET /products/{id} differ by token
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

  // admin: POST /products   create new
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductAdminDTO> create(@Valid @RequestBody ProductAdminUpsertReq req) {
    return ResponseEntity.ok(adminProductService.create(req));
  }

  // admin：PUT /products/{id}  full update
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductAdminDTO> update(@PathVariable Long id,
      @Valid @RequestBody ProductAdminUpsertReq req) {
    return ResponseEntity.ok(adminProductService.update(id, req));
  }

  // admin：PATCH /products/{id}/active  de/activate
  @PatchMapping("/{id}/active")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductAdminDTO> toggleActive(@PathVariable Long id,
      @RequestParam boolean value) {
    return ResponseEntity.ok(adminProductService.toggleActive(id, value));
  }

  // admin: partial update
  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductAdminDTO> patchProduct(@PathVariable Long id,
      @RequestBody ProductAdminPatchReq req) {
    return ResponseEntity.ok(adminProductService.patch(id, req));
  }

  // util: check if a user has a specific role
  public static boolean hasRole(Authentication auth, String role) {
    if (auth == null) return false;
    Collection<?> authorities = auth.getAuthorities();
    if (authorities == null) return false;
    return authorities.stream().anyMatch(a -> role.equals(a.toString()));
  }

  // find most frequent purchase
  // GET /products/frequent/{n}
  @GetMapping("/frequent/{n}")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<List<ProductStatDTO>> topFrequent(Authentication authentication,
      @PathVariable int n) {
    String username = authentication.getName();
    return ResponseEntity.ok(reportService.topFrequent(username, n));
  }

  // GET /products/recent/{n}
  @GetMapping("/recent/{n}")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<List<ProductStatDTO>> topRecent(Authentication authentication,
      @PathVariable int n) {
    String username = authentication.getName();
    return ResponseEntity.ok(reportService.topRecent(username, n));
  }

  // admin: most popular, by sale quantity
  @GetMapping("/popular/{n}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<ProductStatDTO>> topPopular(@PathVariable int n) {
    return ResponseEntity.ok(reportService.topPopular(n));
  }

  // admin：most profitable, by profit
  @GetMapping("/profit/{n}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<ProductProfitDTO>> topProfit(@PathVariable int n) {
    return ResponseEntity.ok(reportService.topProfit(n));
  }

}
