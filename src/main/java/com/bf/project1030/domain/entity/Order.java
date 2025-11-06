// src/main/java/com/example/shop/entity/Order.java
package com.bf.project1030.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  @Column(nullable = false)
  private Double totalPrice;

  @Column(nullable = false)
  private Instant createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
  }
}
