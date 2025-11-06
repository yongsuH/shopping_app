package com.bf.project1030.controller;

import com.bf.project1030.DTO.ProductUserDTO;
import com.bf.project1030.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/watchlist")
public class WatchlistController {

  private final WatchlistService watchlistService;

  // add to watchlist：POST /watchlist/{productId}
  @PostMapping("/product/{productId}")
  public ResponseEntity<Void> add(Authentication authentication, @PathVariable Long productId) {
    watchlistService.add(authentication, productId);
    return ResponseEntity.ok().build();
  }

  // remove from watch list：DELETE /watchlist/{productId}
  @DeleteMapping("/product/{productId}")
  public ResponseEntity<Void> remove(Authentication authentication, @PathVariable Long productId) {
    watchlistService.remove(authentication, productId);
    return ResponseEntity.noContent().build();
  }

  // list：GET /api/watchlist
  @GetMapping("/products/all")
  public ResponseEntity<List<ProductUserDTO>> list(Authentication authentication) {
    return ResponseEntity.ok(watchlistService.list(authentication));
  }
}
