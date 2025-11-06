// src/main/java/com/example/shop/controller/OrderController.java
package com.bf.project1030.controller;

import com.bf.project1030.DTO.OrderDTO;
import com.bf.project1030.domain.entity.Order;
import com.bf.project1030.exception.ResourceNotFoundException;
import com.bf.project1030.repository.UserDao;
import com.bf.project1030.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

  private final OrderService orderService;
  private final UserDao userDao;   // 用来把 username -> userId

  // 下单：不再用 /place/{userId}，改为 /place，userId 从登录用户获得
  @PostMapping("/place")
  public ResponseEntity<OrderDTO> placeOrder(
      Authentication authentication,                        // Spring Security 注入
      @RequestBody List<OrderService.OrderItemRequest> items) {

    String username = authentication.getName();             // 来自 JWT
    Long userId = userDao.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"))
        .getUserId();

    return ResponseEntity.ok(orderService.placeOrder(userId, items));
  }

  // 查询自己的订单：/api/orders/my
  @GetMapping("/my")
  public ResponseEntity<List<OrderDTO>> myOrders(Authentication authentication) {
    String username = authentication.getName();
    Long userId = userDao.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"))
        .getUserId();

    return ResponseEntity.ok(orderService.getOrdersByUser(userId));
  }
}
