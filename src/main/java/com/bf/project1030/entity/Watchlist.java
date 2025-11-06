package com.bf.project1030.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
    name = "user_watchlist",
    uniqueConstraints = @UniqueConstraint(name = "uk_user_product", columnNames = {"user_id","product_id"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Watchlist {

  @EmbeddedId
  private WatchlistKey id;

  @ManyToOne(optional = false)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(optional = false)
  @MapsId("productId")
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  void onCreate() {
    this.createdAt = Instant.now();
  }
}
