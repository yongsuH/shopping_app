// src/main/java/com/example/shop/entity/Product.java
package com.bf.project1030.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products",
    indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_quantity", columnList = "quantity"),
        @Index(name = "idx_product_active", columnList = "active")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false, length = 120, unique = true)
  private String name;

  @NotBlank
  @Column(nullable = false, length = 1024)
  private String description;

  @NotNull
  @DecimalMin("0.00")
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal wholesalePrice;

  @NotNull
  @DecimalMin("0.00")
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal retailPrice;

  @Min(0)
  @Column(nullable = false)
  private Integer quantity;

  @Builder.Default
  @Column(nullable = false)
  private Boolean active = true;     //switch

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  public void prePersist() {
    Instant now = Instant.now();
    this.createdAt = now;
    this.updatedAt = now;
  }
  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }
}
