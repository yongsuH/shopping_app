package com.bf.project1030.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

  private final SecretKey key;
  private final long expirationMs = 1000L * 60 * 60; // 1小时

  // 从 application.properties 读取密钥
  public JwtUtil(@Value("${app.jwt.secret}") String secret) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  /** 生成用户token，可带角色（USER / ADMIN） */
  public String generateToken(String username, String role) {
    return Jwts.builder()
        .subject(username)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expirationMs))
        .claim("role", role) // 👈 把角色放进 payload
        .signWith(key)
        .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  public String extractRole(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
    return claims.get("role", String.class);
  }
}
