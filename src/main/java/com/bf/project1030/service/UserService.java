package com.bf.project1030.service;

import com.bf.project1030.domain.entity.User;
import com.bf.project1030.exception.DuplicateResourceException;
import com.bf.project1030.repository.UserDao;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserDao userDao;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
    this.userDao = userDao;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public void register(User user) {
    if (userDao.existsByUsername(user.getUsername())) {
      throw new DuplicateResourceException("Username already exists");
    }
    if (userDao.existsByEmail(user.getEmail())) {
      throw new DuplicateResourceException("Email already exists");
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userDao.save(user);
  }

}
