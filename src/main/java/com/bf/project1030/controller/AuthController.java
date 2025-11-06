package com.bf.project1030.controller;

import com.bf.project1030.entity.User;
import com.bf.project1030.exception.InvalidCredentialsException;
import com.bf.project1030.exception.ResourceNotFoundException;
import com.bf.project1030.repository.UserDao;
import com.bf.project1030.security.JwtUtil;
import com.bf.project1030.service.AuthService;
import com.bf.project1030.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
public class AuthController {

  private final UserService userService;
  private final JwtUtil jwtUtil;
  private final AuthService authService;
  private final UserDao userDao;

  public AuthController(UserService userService, JwtUtil jwtUtil, AuthService authService,
      UserDao userDao) {
    this.userService = userService;
    this.jwtUtil = jwtUtil;
    this.authService = authService;
    this.userDao = userDao;
  }

  // save to database
  @PostMapping("/signup")
  public ResponseEntity<String> signup(@Valid @RequestBody User user) {
    userService.register(user);
    return ResponseEntity.ok("User registered successfully");
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginReq req) {
    if (authService.authenticate(req.username(), req.password())) {
      var user = userDao.findByUsername(req.username())
          .orElseThrow(() -> new ResourceNotFoundException("User not found"));
      String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name()); // ADMIN / USER
      return ResponseEntity.ok(Map.of(
          "token", token,
          "role", user.getRole().name()
      ));
    }
    throw new InvalidCredentialsException();
  }

  public record LoginReq(String username, String password) {}

  @GetMapping("/me")
  public ResponseEntity<String> me() {
    // passed JWT auth
    return ResponseEntity.ok("OK");
  }
}
