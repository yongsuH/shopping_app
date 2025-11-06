// src/main/java/com/example/shop/controller/ProductController.java
package com.bf.project1030.controller;

import com.bf.project1030.DTO.ProductUserDTO;
import com.bf.project1030.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;

  @GetMapping("/all")
  public ResponseEntity<List<ProductUserDTO>> allForUser() {
    return ResponseEntity.ok(productService.getAllForUser());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductUserDTO> oneForUser(@PathVariable Long id) {
    return ResponseEntity.ok(productService.getOneForUser(id));
  }
}
