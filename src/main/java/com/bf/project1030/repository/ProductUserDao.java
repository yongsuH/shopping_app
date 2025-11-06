// src/main/java/com/example/shop/dao/ProductDao.java
package com.bf.project1030.repository;

import com.bf.project1030.domain.entity.Product;
import com.bf.project1030.exception.ResourceNotFoundException;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class ProductUserDao {

  @PersistenceContext
  private EntityManager em;

  public List<Product> findAllInStockForUser() {
    // Criteria 示例
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Product> cq = cb.createQuery(Product.class);
    Root<Product> root = cq.from(Product.class);
    cq.select(root).where(
        cb.and(cb.equal(root.get("active"), true),
            cb.greaterThan(root.get("quantity"), 0))
    ).orderBy(cb.asc(root.get("name")));
    return em.createQuery(cq).getResultList();
  }

  public Optional<Product> findByIdForUserInStock(Long id) {
    String hql = """
      from Product p
      where p.id = :id and p.active = true and p.quantity > 0
      """;
    List<Product> list = em.createQuery(hql, Product.class)
        .setParameter("id", id)
        .getResultList();
    return list.stream().findFirst();
  }

  public Optional<Product> findByIdForUserActive(Long id) {
    String hql = """
      from Product p
      where p.id = :id and p.active = true
      """;
    List<Product> list = em.createQuery(hql, Product.class)
        .setParameter("id", id)
        .getResultList();
    return list.stream().findFirst();
  }

  /* ---------- 用户端已有方法（示例） ----------
       public List<Product> findAllInStockForUser() { ... }
       public Optional<Product> findByIdForUser(Long id) { ... }
       ----------------------------------------- */

  // 管理端：分页列表（按 id desc）
  public List<Product> findAllForAdmin(int page, int size) {
    String jpql = "from Product p order by p.id desc";
    return em.createQuery(jpql, Product.class)
        .setFirstResult(page * size)
        .setMaxResults(size)
        .getResultList();
  }

  // 管理端：根据 id 查
  public Optional<Product> findById(Long id) {
    return Optional.ofNullable(em.find(Product.class, id));
  }

  // 保存或更新（无 interface 的风格）
  public Product save(Product p) {
    if (p.getId() == null) {
      em.persist(p);
      return p;
    } else {
      return em.merge(p);
    }
  }

  // 管理端：强制查不到就抛 404（方便 Service 复用）
  public Product require(Long id) {
    return findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
  }
}
