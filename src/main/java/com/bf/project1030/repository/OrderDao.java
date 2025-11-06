package com.bf.project1030.repository;

import com.bf.project1030.entity.Order;
import jakarta.persistence.*;
import java.util.Optional;
import org.springframework.stereotype.Repository;

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

  public Optional<Order> findById(Long id) {
    return Optional.ofNullable(em.find(Order.class, id));
  }

  public List<Order> findAllPaged(int page, int size) {
    String hql = "from Order o order by o.createdAt desc";
    return em.createQuery(hql, Order.class)
        .setFirstResult(page * size) // start from
        .setMaxResults(size)         // how many
        .getResultList();
  }

  public Order findByIdForUpdate(Long id) {
    return em.find(Order.class, id, LockModeType.PESSIMISTIC_WRITE);
  }
}
