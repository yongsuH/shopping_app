package com.bf.project1030.repository;

import com.bf.project1030.DTO.ProductProfitDTO;
import com.bf.project1030.DTO.ProductStatDTO;
import com.bf.project1030.entity.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportDao {

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

  // admin：by sales（completed sale quantity）top n
  public List<ProductStatDTO> topPopularProducts(int topN) {
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
      where o.status = :completed
      group by p.id, p.name
      order by sum(oi.quantity) desc, p.id asc
      """;
    return em.createQuery(jpql, ProductStatDTO.class)
        .setParameter("completed", OrderStatus.COMPLETED)
        .setMaxResults(topN)
        .getResultList();
  }

  // admin：by total profit（sales × (retail - wholesale)）top n
  public List<ProductProfitDTO> topProfitableProducts(int topN) {
    String jpql = """
      select new com.bf.project1030.DTO.ProductProfitDTO(
        p.id,
        p.name,
        sum( (p.retailPrice - p.wholesalePrice) * oi.quantity )
      )
      from OrderItem oi
        join oi.order o
        join oi.product p
      where o.status = :completed
      group by p.id, p.name
      order by sum( (p.retailPrice - p.wholesalePrice) * oi.quantity ) desc, p.id asc
      """;

    return em.createQuery(jpql, ProductProfitDTO.class)
        .setParameter("completed", OrderStatus.COMPLETED)
        .setMaxResults(topN)
        .getResultList();
  }

}
