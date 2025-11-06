// src/main/java/com/example/shop/dao/ProductDao.java
package com.bf.project1030.repository;

import com.bf.project1030.entity.Product;
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
    // criteria api
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

  // find by name
  public Optional<Product> findByName(String name) {
    String hql = "from Product p where p.name = :name";
    List<Product> result = em.createQuery(hql, Product.class)
        .setParameter("name", name)
        .getResultList();
    return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
  }

  // check existence by name
  public boolean existsByName(String name) {
    String hql = "select count(p) from Product p where p.name = :name";
    Long count = em.createQuery(hql, Long.class)
        .setParameter("name", name)
        .getSingleResult();
    return count > 0;
  }

  // admin: pages list, desc by id
  public List<Product> findAllForAdmin(int page, int size) {
    String jpql = "from Product p order by p.id desc";
    return em.createQuery(jpql, Product.class)
        .setFirstResult(page * size)
        .setMaxResults(size)
        .getResultList();
  }

  //
  public Optional<Product> findById(Long id) {
    return Optional.ofNullable(em.find(Product.class, id));
  }

  // save or update
  public Product save(Product p) {
    if (p.getId() == null) {
      em.persist(p);
      return p;
    } else {
      return em.merge(p);
    }
  }

  // throw exception if not found
  public Product require(Long id) {
    return findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
  }
}
