package com.bf.project1030.service;

import com.bf.project1030.entity.User;
import com.bf.project1030.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserDao userDao;
  private final PasswordEncoder passwordEncoder;

  public boolean authenticate(String username, String rawPassword) {
    User u = userDao.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    return passwordEncoder.matches(rawPassword, u.getPassword());
  }
}
