// src/main/java/com/example/shop/service/OrderService.java
package com.bf.project1030.service;

import com.bf.project1030.DTO.OrderDTO;
import com.bf.project1030.DTO.OrderItemDTO;
import com.bf.project1030.entity.Order;
import com.bf.project1030.entity.OrderItem;
import com.bf.project1030.entity.OrderStatus;
import com.bf.project1030.entity.Product;
import com.bf.project1030.entity.User;
import com.bf.project1030.exception.NotEnoughInventoryException;
import com.bf.project1030.exception.InvalidOrderStatusTransitionException;
import com.bf.project1030.repository.*;
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

  // admin can access all orders
  public OrderDTO getOrderByIdForAdmin(Long id) {
    Order order = orderDao.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    return toDTO(order);
  }

  // user can only access their own order
  public OrderDTO getOrderByIdForUser(String username, Long id) {
    Order order = orderDao.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

    // Reject if not the user
    if (!order.getUser().getUsername().equals(username)) {
      throw new AccessDeniedException("You do not have permission to access this order");
    }

    return toDTO(order);
  }

  public List<OrderDTO> getAllOrdersPaged(int page, int size) {
    return orderDao.findAllPaged(page, size).stream()
        .map(OrderService::toDTO)
        .toList();
  }

  private static OrderDTO toDTO(Order order) {
    return new OrderDTO(
        order.getId(),
        order.getTotalPrice(),
        order.getCreatedAt(),
        order.getUser().getUserId(),
        order.getUser().getUsername(),
        order.getStatus(),
        order.getItems().stream()
            .map(item -> new OrderItemDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPriceSnapshot()))
            .toList()
    );
  }

  // Completing a order：PROCESSING -> COMPLETED
  @Transactional
  public OrderDTO complete(Long id) {
    var order = orderDao.findByIdForUpdate(id);
    if (order == null) {
      throw new ResourceNotFoundException("Order not found");
    }
    if (order.getStatus() == OrderStatus.CANCELED) {
      throw new InvalidOrderStatusTransitionException("Canceled order cannot be completed");
    }
    // already completed
    if (order.getStatus() == OrderStatus.COMPLETED) {
      throw new InvalidOrderStatusTransitionException("Order is already completed");
    }
    //if (order.getStatus() != OrderStatus.PROCESSING) {
    //  throw new InvalidOrderStatusTransitionException("Only PROCESSING order can be completed");
    //}

    order.setStatus(OrderStatus.COMPLETED);
    return toDTO(order);
  }

  // Cancel order：PROCESSING -> CANCELED，add back stock
  @Transactional
  public OrderDTO cancel(Long id) {
    var order = orderDao.findByIdForUpdate(id);
    if (order == null) {
      throw new ResourceNotFoundException("Order not found");
    }

    if (order.getStatus() == OrderStatus.COMPLETED) {
      throw new InvalidOrderStatusTransitionException("Completed order cannot be cancelled");
    }

    // order already canceled
    if (order.getStatus() == OrderStatus.CANCELED) {
      throw new InvalidOrderStatusTransitionException("Order is already canceled");
    }

    //
    if (order.getStatus() != OrderStatus.PROCESSING) {
      throw new InvalidOrderStatusTransitionException(
          "Only PROCESSING orders can be canceled");
    }

    // add back stock
    for (OrderItem item : order.getItems()) {
      var product = item.getProduct();
      product.setQuantity(product.getQuantity() + item.getQuantity());
    }

    order.setStatus(OrderStatus.CANCELED);
    return toDTO(order);
  }

  // OrderItemRequest
  public record OrderItemRequest(Long productId, Integer quantity) {}

}
