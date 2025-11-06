package com.bf.project1030.service;

import com.bf.project1030.DTO.ProductUserDTO;
import com.bf.project1030.entity.Product;
import com.bf.project1030.entity.User;
import com.bf.project1030.entity.Watchlist;
import com.bf.project1030.entity.WatchlistKey;
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

  // get current user from authentication
  private User currentUser(Authentication authentication) {
    String username = authentication.getName(); // name from JWT
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
    // return user DTO
    return watchlistDao.findProductsByUserId(user.getUserId()).stream()
        // filter out of stock and inactive products
        .filter(p -> Boolean.TRUE.equals(p.getActive()) &&
            p.getQuantity() != null && p.getQuantity() > 0)
        .map(p -> new ProductUserDTO(p.getId(), p.getName(), p.getDescription(), p.getRetailPrice()))
        .toList();
  }
}
