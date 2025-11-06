package com.bf.project1030.bootstrap;

import com.bf.project1030.domain.entity.Role;
import com.bf.project1030.domain.entity.User;
import com.bf.project1030.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

  private final UserDao userDao;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(String... args) {
    if (!userDao.existsByUsername("admin")) {
      User admin = new User();
      admin.setUsername("admin");
      admin.setEmail("admin@example.com");
      admin.setPassword(passwordEncoder.encode("admin123"));
      admin.setRole(Role.ADMIN);
      userDao.save(admin);
      System.out.println("✅ Admin account created: admin / admin123");
    }
  }
}
