// src/main/java/com/example/shop/entity/OrderItem.java
package com.bf.project1030.domain.entity;

import com.bf.project1030.domain.entity.Order;
import com.bf.project1030.domain.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "order_id")
  private Order order;

  @ManyToOne(optional = false)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal priceSnapshot;   // 下单时的零售价快照
}
