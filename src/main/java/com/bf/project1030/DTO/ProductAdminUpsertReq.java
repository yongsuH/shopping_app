// src/main/java/com/bf/project1030/DTO/ProductAdminUpsertReq.java
package com.bf.project1030.DTO;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductAdminUpsertReq(
    @NotBlank @Size(max = 255) String name,
    @NotBlank @Size(max = 1024) String description,
    @NotNull @DecimalMin("0.00") BigDecimal wholesalePrice,
    @NotNull @DecimalMin("0.00") BigDecimal retailPrice,
    @NotNull @Min(0) Integer quantity,
    Boolean active
) {
  public ProductAdminUpsertReq {
    if (active == null) active = true;
  }
}
