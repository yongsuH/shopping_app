package com.bf.project1030.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
  @NotBlank
  private String username;
  @NotBlank
  private String password;
}
