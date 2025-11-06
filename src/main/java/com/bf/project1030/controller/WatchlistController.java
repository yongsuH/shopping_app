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

  /** 添加收藏：POST /api/watchlist/{productId} */
  @PostMapping("/{productId}")
  public ResponseEntity<Void> add(Authentication authentication, @PathVariable Long productId) {
    watchlistService.add(authentication, productId);
    return ResponseEntity.ok().build();
  }

  /** 取消收藏：DELETE /api/watchlist/{productId} */
  @DeleteMapping("/{productId}")
  public ResponseEntity<Void> remove(Authentication authentication, @PathVariable Long productId) {
    watchlistService.remove(authentication, productId);
    return ResponseEntity.noContent().build();
  }

  /** 列表：GET /api/watchlist */
  @GetMapping
  public ResponseEntity<List<ProductUserDTO>> list(Authentication authentication) {
    return ResponseEntity.ok(watchlistService.list(authentication));
  }
}
