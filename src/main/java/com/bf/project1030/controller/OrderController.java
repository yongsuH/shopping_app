// src/main/java/com/example/shop/controller/OrderController.java
package com.bf.project1030.controller;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

import com.bf.project1030.DTO.OrderDTO;
import com.bf.project1030.DTO.OrderRequest;
import com.bf.project1030.DTO.ProductAdminDTO;
import com.bf.project1030.DTO.ProductUserDTO;
import com.bf.project1030.domain.entity.Order;
import com.bf.project1030.exception.ResourceNotFoundException;
import com.bf.project1030.repository.UserDao;
import com.bf.project1030.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

  private final OrderService orderService;
  private final UserDao userDao;

  // Place order
  @PostMapping("")
  public ResponseEntity<OrderDTO> placeOrder(
      Authentication authentication,
      @RequestBody OrderRequest req) {

    String username = authentication.getName();
    Long userId = userDao.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"))
        .getUserId();

    return ResponseEntity.ok(orderService.placeOrder(userId, req.order()));
  }

  // find orders (USER: own orders; ADMIN: all orders paged, 5 per page)
  @GetMapping("/all")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<List<OrderDTO>> myOrders(Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (hasRole(authentication, "ROLE_ADMIN")) {
      // admin sees all orders，5 per page
      return ResponseEntity.ok(orderService.getAllOrdersPaged(page, 5));
    } else {
      // user: sees their own order
      String username = authentication.getName();
      Long userId = userDao.findByUsername(username)
          .orElseThrow(() -> new ResourceNotFoundException("User not found"))
          .getUserId();
      return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }
  }

  // find order by id
  @GetMapping("{id}")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<OrderDTO> getOrder(Authentication authentication, @PathVariable Long id) {
    if (ProductController.hasRole(authentication, "ROLE_ADMIN")) {
      // admin
      OrderDTO dto = orderService.getOrderByIdForAdmin(id);
      return ResponseEntity.ok(dto);
    } else {
      // user
      String username = authentication.getName();  // JWT 解析出的用户名
      OrderDTO dto = orderService.getOrderByIdForUser(username, id);
      return ResponseEntity.ok(dto);
    }
  }

  // PATCH /orders/{id}/complete
  @PatchMapping("/{id}/complete")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<OrderDTO> complete(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.complete(id));
  }

  // PATCH /orders/{id}/cancel
  @PatchMapping("/{id}/cancel")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<OrderDTO> cancel(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.cancel(id));
  }

  // util method
  private boolean hasRole(Authentication auth, String role) {
    if (auth == null || auth.getAuthorities() == null) return false;
    return auth.getAuthorities().stream()
        .anyMatch(a -> role.equals(a.getAuthority()));
  }
}
