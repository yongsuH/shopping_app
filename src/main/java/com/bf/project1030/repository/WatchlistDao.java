package com.bf.project1030.repository;

import com.bf.project1030.domain.entity.Watchlist;
import com.bf.project1030.domain.entity.WatchlistKey;
import com.bf.project1030.domain.entity.Product;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class WatchlistDao {

  @PersistenceContext
  private EntityManager em;

  public Optional<Watchlist> findById(WatchlistKey id) {
    return Optional.ofNullable(em.find(Watchlist.class, id));
  }

  public Watchlist save(Watchlist w) {
    return em.merge(w); // merge 可兼容新老
  }

  public void deleteById(WatchlistKey id) {
    findById(id).ifPresent(em::remove);
  }

  public boolean exists(Long userId, Long productId) {
    String jpql = """
            select count(w) from Watchlist w 
            where w.id.userId = :uid and w.id.productId = :pid
            """;
    Long c = em.createQuery(jpql, Long.class)
        .setParameter("uid", userId)
        .setParameter("pid", productId)
        .getSingleResult();
    return c != null && c > 0;
  }

  public List<Product> findProductsByUserId(Long userId) {
    String jpql = """
            select w.product 
            from Watchlist w 
            where w.id.userId = :uid
            order by w.createdAt desc
            """;
    return em.createQuery(jpql, Product.class)
        .setParameter("uid", userId)
        .getResultList();
  }
}
