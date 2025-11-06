package com.bf.project1030.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  public JwtAuthFilter(JwtUtil jwtUtil){ this.jwtUtil = jwtUtil; }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    System.out.println("DEBUG: JwtAuthFilter is running for URL: " + req.getRequestURI());
    // 如果上下文里已经有认证，就不再重复解析
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String header = req.getHeader(HttpHeaders.AUTHORIZATION);
      if (header != null && header.startsWith("Bearer ")) {
        String token = header.substring(7);
        try {
          String username = jwtUtil.extractUsername(token);
          String role = jwtUtil.extractRole(token);           //

          if (username != null && !username.isBlank() && role != null && !role.isBlank()) {
            var authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + role)    //
            );
            var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(auth);
          }
        } catch (Exception e) {
          // token 无效/过期：不设置认证，留给 Security 的 entryPoint 统一返回 401
          SecurityContextHolder.clearContext();
        }
      }
    }
    chain.doFilter(req, res);
  }
}
