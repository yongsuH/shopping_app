package com.bf.project1030.repository;

import com.bf.project1030.DTO.ProductStatDTO;
import com.bf.project1030.domain.entity.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserStatDao {

  @PersistenceContext
  private EntityManager em;

  // desc by total quantity bought
  public List<ProductStatDTO> topFrequentProducts(String username, int topN) {
    String jpql = """
        select new com.bf.project1030.DTO.ProductStatDTO(
          p.id,
          p.name,
          sum(oi.quantity),
          null
        )
        from OrderItem oi
          join oi.order o
          join oi.product p
          join o.user u
        where u.username = :username
          and o.status <> :canceled
        group by p.id, p.name
        order by sum(oi.quantity) desc, p.id asc
        """;
    return em.createQuery(jpql, ProductStatDTO.class)
        .setParameter("username", username)
        .setParameter("canceled", OrderStatus.CANCELED)
        .setMaxResults(topN)
        .getResultList();
  }

  // desc by bought time
  public List<ProductStatDTO> topRecentProducts(String username, int topN) {
    String jpql = """
        select new com.bf.project1030.DTO.ProductStatDTO(
          p.id,
          p.name,
          0,
          max(o.createdAt)
        )
        from OrderItem oi
          join oi.order o
          join oi.product p
          join o.user u
        where u.username = :username
          and o.status <> :canceled
        group by p.id, p.name
        order by max(o.createdAt) desc, p.id asc
        """;
    return em.createQuery(jpql, ProductStatDTO.class)
        .setParameter("username", username)
        .setParameter("canceled", OrderStatus.CANCELED)
        .setMaxResults(topN)
        .getResultList();
  }
}
