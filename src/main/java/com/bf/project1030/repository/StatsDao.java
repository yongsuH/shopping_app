// src/main/java/com/bf/project1030/repository/StatsDao.java
package com.bf.project1030.repository;

import com.bf.project1030.DTO.RevenueDailyPoint;
import com.bf.project1030.DTO.TopProductDTO;
import com.bf.project1030.DTO.StatsOverviewDTO;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class StatsDao {

  @PersistenceContext
  private EntityManager em;

  public StatsOverviewDTO overview() {
    // total revenue：sum(item.priceSnapshot * item.quantity)
    String revenueJpql = """
      select coalesce(sum(oi.priceSnapshot * oi.quantity), 0)
      from OrderItem oi
      """;
    BigDecimal totalRevenue = em.createQuery(revenueJpql, BigDecimal.class).getSingleResult();

    String countOrders = "select count(o) from Order o";
    Long ordersCount = em.createQuery(countOrders, Long.class).getSingleResult();

    String itemsSoldJpql = "select coalesce(sum(oi.quantity), 0) from OrderItem oi";
    Long itemsSold = em.createQuery(itemsSoldJpql, Long.class).getSingleResult();

    return new StatsOverviewDTO(ordersCount, itemsSold, totalRevenue);
  }

  public List<RevenueDailyPoint> revenueDaily(LocalDate from, LocalDate to) {
    // seperate by date Order.createdAt, from/to inclusive
    String jpql = """
      select function('date', o.createdAt) as d,
             coalesce(sum(oi.priceSnapshot * oi.quantity), 0)
      from Order o
      join o.items oi
      where function('date', o.createdAt) between :from and :to
      group by function('date', o.createdAt)
      order by function('date', o.createdAt)
      """;
    List<Object[]> rows = em.createQuery(jpql, Object[].class)
        .setParameter("from", from)
        .setParameter("to", to)
        .getResultList();

    List<RevenueDailyPoint> out = new ArrayList<>();
    for (Object[] r : rows) {
      out.add(new RevenueDailyPoint((LocalDate) r[0], (BigDecimal) r[1]));
    }
    return out;
  }

  public List<TopProductDTO> topProducts(int limit, boolean byRevenue) {
    // Product dimentions
    String jpql = byRevenue ? """
      select p.id, p.name,
             coalesce(sum(oi.quantity), 0) as qty,
             coalesce(sum(oi.priceSnapshot * oi.quantity), 0) as rev
      from OrderItem oi
      join oi.product p
      group by p.id, p.name
      order by rev desc
      """ : """
      select p.id, p.name,
             coalesce(sum(oi.quantity), 0) as qty,
             coalesce(sum(oi.priceSnapshot * oi.quantity), 0) as rev
      from OrderItem oi
      join oi.product p
      group by p.id, p.name
      order by qty desc
      """;

    List<Object[]> rows = em.createQuery(jpql, Object[].class)
        .setMaxResults(Math.max(1, limit))
        .getResultList();

    List<TopProductDTO> out = new ArrayList<>();
    for (Object[] r : rows) {
      out.add(new TopProductDTO(
          (Long) r[0],
          (String) r[1],
          (Long) r[2],
          (BigDecimal) r[3]
      ));
    }
    return out;
  }
}
