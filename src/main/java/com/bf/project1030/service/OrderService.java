// src/main/java/com/example/shop/service/OrderService.java
package com.bf.project1030.service;

import com.bf.project1030.DTO.OrderDTO;
import com.bf.project1030.DTO.OrderItemDTO;
import com.bf.project1030.exception.NotEnoughInventoryException;
import com.bf.project1030.repository.*;
import com.bf.project1030.domain.entity.*;
import com.bf.project1030.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderDao orderDao;
  private final ProductUserDao productUserDao;
  private final UserDao userDao;

  @Transactional
  public OrderDTO placeOrder(Long userId, List<OrderItemRequest> items) {
    User user = userDao.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Order order = new Order();
    order.setUser(user);

    double total = 0.0;

    for (OrderItemRequest req : items) {
      Product p = productUserDao.findByIdForUserInStock(req.productId())
          .orElseThrow(() -> new ResourceNotFoundException("Product not found or out of stock"));

      if (p.getQuantity() < req.quantity()) {
        throw new NotEnoughInventoryException("Not enough stock for " + p.getName());
      }

      // deduct stock
      p.setQuantity(p.getQuantity() - req.quantity());

      OrderItem item = new OrderItem();
      item.setOrder(order);
      item.setProduct(p);
      item.setQuantity(req.quantity());
      item.setPriceSnapshot(p.getRetailPrice());
      order.getItems().add(item);

      total += p.getRetailPrice().doubleValue() * req.quantity();
    }

    order.setTotalPrice(total);
    orderDao.save(order);
    return toDTO(order);
  }

  public List<OrderDTO> getOrdersByUser(Long userId) {
    return orderDao.findByUserId(userId).stream()
        .map(OrderService::toDTO)
        .toList();
  }

  // 管理员查任意订单
  public OrderDTO getOrderByIdForAdmin(Long id) {
    Order order = orderDao.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    return toDTO(order);
  }

  // 用户只能查自己的订单
  public OrderDTO getOrderByIdForUser(String username, Long id) {
    Order order = orderDao.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

    // 如果订单不属于当前用户，就拒绝
    if (!order.getUser().getUsername().equals(username)) {
      throw new AccessDeniedException("You do not have permission to access this order");
    }

    return toDTO(order);
  }

  private static OrderDTO toDTO(Order order) {
    return new OrderDTO(
        order.getId(),
        order.getTotalPrice(),
        order.getCreatedAt(),
        order.getUser().getUserId(),
        order.getUser().getUsername(),
        order.getItems().stream()
            .map(item -> new OrderItemDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPriceSnapshot()))
            .toList()
    );
  }

  // OrderItemRequest
  public record OrderItemRequest(Long productId, Integer quantity) {}

}
