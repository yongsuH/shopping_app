package com.bf.project1030.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> { // 未认证 → 401
                  res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                  res.setContentType("application/json;charset=UTF-8");
                  res.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"JWT required\"}");
                })
            .accessDeniedHandler((req, res, e) -> {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Access denied\"}");
            })
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/signup", "/login").permitAll()
            .requestMatchers("/products/all", "/products/*").permitAll()
            .requestMatchers("/orders/**").authenticated()   // 订单相关必须登录
            .requestMatchers("/watchlist/**").authenticated()
            .requestMatchers("/admin/**").hasRole("ADMIN")

            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
