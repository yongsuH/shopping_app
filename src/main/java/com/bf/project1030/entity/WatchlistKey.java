package com.bf.project1030.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class WatchlistKey implements Serializable {
  private Long userId;
  private Long productId;
}
