package com.bf.project1030.repository;

import com.bf.project1030.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UserDao {

  @PersistenceContext
  private EntityManager em;

  // check existence by username
  public boolean existsByUsername(String username) {
    Long cnt = em.createQuery(
            "select count(u) from User u where u.username = :username", Long.class)
        .setParameter("username", username)
        .getSingleResult();
    return cnt > 0;
  }

  // check existence by email
  public boolean existsByEmail(String email) {
    Long cnt = em.createQuery(
            "select count(u) from User u where u.email = :email", Long.class)
        .setParameter("email", email)
        .getSingleResult();
    return cnt > 0;
  }

  // save new user
  public void save(User user) {
    em.persist(user);
  }

  // find by username
  public Optional<User> findByUsername(String username) {
    return em.createQuery(
            "select u from User u where u.username = :username", User.class)
        .setParameter("username", username)
        .getResultStream()
        .findFirst();
  }

  // find by id
  public Optional<User> findById(Long id) {
    return Optional.ofNullable(em.find(User.class, id));
  }
}
