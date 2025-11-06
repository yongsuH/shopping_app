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

  // find their own order
  @GetMapping("/all")
  public ResponseEntity<List<OrderDTO>> myOrders(Authentication authentication) {
    String username = authentication.getName();
    Long userId = userDao.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"))
        .getUserId();

    return ResponseEntity.ok(orderService.getOrdersByUser(userId));
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
}
