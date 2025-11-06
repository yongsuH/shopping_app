package com.bf.project1030.repository;

import com.bf.project1030.domain.entity.Order;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class OrderDao {
  @PersistenceContext private EntityManager em;

  public Order save(Order order) {
    em.persist(order);
    return order;
  }

  public List<Order> findByUserId(Long userId) {
    String hql = "from Order o where o.user.id = :uid order by o.createdAt desc";
    return em.createQuery(hql, Order.class)
        .setParameter("uid", userId)
        .getResultList();
  }
}
