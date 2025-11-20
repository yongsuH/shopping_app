// src/main/java/com/example/shop/controller/OrderController.java
package com.bf.project1030.controller;

import com.bf.project1030.DTO.OrderDTO;
import com.bf.project1030.DTO.OrderRequest;
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
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size) {
    if (hasRole(authentication, "ROLE_ADMIN")) {
      // admin sees all orders，default 5 per page
      return ResponseEntity.ok(orderService.getAllOrdersPaged(page, size));
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
  public ResponseEntity<OrderDTO> complete(Authentication authentication, @PathVariable Long id) {
    return ResponseEntity.ok(orderService.complete(authentication, id));
  }

  // PATCH /orders/{id}/cancel
  @PatchMapping("/{id}/cancel")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<OrderDTO> cancel(Authentication authentication, @PathVariable Long id) {
    return ResponseEntity.ok(orderService.cancel(authentication, id));
  }

  // util method
  private boolean hasRole(Authentication auth, String role) {
    if (auth == null || auth.getAuthorities() == null) return false;
    return auth.getAuthorities().stream()
        .anyMatch(a -> role.equals(a.getAuthority()));
  }
}
