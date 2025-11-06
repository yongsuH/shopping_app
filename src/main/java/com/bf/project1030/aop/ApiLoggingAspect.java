package com.bf.project1030.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class ApiLoggingAspect {

  // catch controller layer
  @Pointcut("within(com.bf.project1030.controller..*)")
  public void controllerLayer() {}

  // No log tag
  @Pointcut("@within(com.bf.project1030.aop.NoLog) || @annotation(com.bf.project1030.aop.NoLog)")
  public void noLog() {}

  @Around("controllerLayer() && !noLog()")
  public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
    long start = System.currentTimeMillis();
    String traceId = UUID.randomUUID().toString().substring(0, 8);

    MethodSignature ms = (MethodSignature) pjp.getSignature();
    String classMethod = ms.getDeclaringType().getSimpleName() + "." + ms.getName();

    String user = currentUserSafe();

    // parsing args
    String argsStr = maskAndLimitArgs(pjp.getArgs(), 2000);

    log.info("[{}] → {} user={} args={}", traceId, classMethod, user, argsStr);

    Object result = null;
    int statusGuess = 200;
    try {
      result = pjp.proceed();

      // ResponseEntity --> take status code
      if (result instanceof ResponseEntity<?> resp) {
        statusGuess = resp.getStatusCode().value();
      }

      long took = System.currentTimeMillis() - start;
      String outStr = limitString(safeToString(result), 2000);
      log.info("[{}] ← {} status={} took={}ms body={}",
          traceId, classMethod, statusGuess, took, outStr);
      return result;
    } catch (Throwable ex) {
      long took = System.currentTimeMillis() - start;
      log.error("[{}] !! {} threw: {} took={}ms", traceId, classMethod, ex.toString(), took, ex);
      throw ex;
    }
  }

  // final exception log
  @AfterThrowing(pointcut = "controllerLayer() && !noLog()", throwing = "ex")
  public void logException(JoinPoint jp, Throwable ex) {
    MethodSignature ms = (MethodSignature) jp.getSignature();
    String classMethod = ms.getDeclaringType().getSimpleName() + "." + ms.getName();
    log.error("## EXCEPTION in {} : {}", classMethod, ex.toString());
  }

  private String currentUserSafe() {
    try {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      return (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
    } catch (Exception e) {
      return "anonymous";
    }
  }

  // insensitive
  private String maskAndLimitArgs(Object[] args, int maxLen) {
    String raw = Arrays.toString(args);
    raw = raw.replaceAll("(?i)Authorization=Bearer\\s+[A-Za-z0-9._-]+", "Authorization=Bearer ***");
    raw = raw.replaceAll("(?i)\"password\"\\s*:\\s*\".*?\"", "\"password\":\"***\"");
    raw = raw.replaceAll("(?i)\"secret\"\\s*:\\s*\".*?\"", "\"secret\":\"***\"");
    return limitString(raw, maxLen);
  }

  private String safeToString(Object obj) {
    try {
      return String.valueOf(obj);
    } catch (Exception e) {
      return "<unprintable>";
    }
  }

  private String limitString(String s, int max) {
    if (s == null) return null;
    return s.length() <= max ? s : s.substring(0, max) + "...(truncated)";
  }
}
