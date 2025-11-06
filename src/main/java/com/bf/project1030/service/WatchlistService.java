package com.bf.project1030.service;

import com.bf.project1030.DTO.ProductUserDTO;
import com.bf.project1030.domain.entity.*;
import com.bf.project1030.exception.ResourceNotFoundException;
import com.bf.project1030.repository.ProductUserDao;
import com.bf.project1030.repository.UserDao;
import com.bf.project1030.repository.WatchlistDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistService {

  private final UserDao userDao;
  private final ProductUserDao productUserDao;
  private final WatchlistDao watchlistDao;

  /** 从 Authentication 获取当前用户对象 */
  private User currentUser(Authentication authentication) {
    String username = authentication.getName(); // JWT 解析后的 subject
    // 假设你有 findByUsername；若用 email，就换成 findByEmail
    return userDao.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  @Transactional
  public void add(Authentication authentication, Long productId) {
    User user = currentUser(authentication);

    // active
    Product product = productUserDao.findByIdForUserActive(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found or out of stock"));

    if (!watchlistDao.exists(user.getUserId(), product.getId())) {
      Watchlist w = Watchlist.builder()
          .id(new WatchlistKey(user.getUserId(), product.getId()))
          .user(user)
          .product(product)
          .build();
      watchlistDao.save(w);
    }
  }

  @Transactional
  public void remove(Authentication authentication, Long productId) {
    User user = currentUser(authentication);
    WatchlistKey key = new WatchlistKey(user.getUserId(), productId);
    watchlistDao.deleteById(key);
  }

  public List<ProductUserDTO> list(Authentication authentication) {
    User user = currentUser(authentication);
    // 仅返回用户端 DTO（不暴露库存/批发价）
    return watchlistDao.findProductsByUserId(user.getUserId()).stream()
        // 也可选择过滤掉已下架/无库存的项（如下）
        .filter(p -> Boolean.TRUE.equals(p.getActive()) &&
            p.getQuantity() != null && p.getQuantity() > 0)
        .map(p -> new ProductUserDTO(p.getId(), p.getName(), p.getDescription(), p.getRetailPrice()))
        .toList();
  }
}
