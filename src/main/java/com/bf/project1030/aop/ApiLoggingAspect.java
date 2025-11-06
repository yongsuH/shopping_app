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

  // 只拦截你项目里的 controller 层（可按需调整）
  @Pointcut("within(com.bf.project1030.controller..*)")
  public void controllerLayer() {}

  // 标注 NoLog 的类或方法不记录
  @Pointcut("@within(com.bf.project1030.aop.NoLog) || @annotation(com.bf.project1030.aop.NoLog)")
  public void noLog() {}

  @Around("controllerLayer() && !noLog()")
  public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
    long start = System.currentTimeMillis();
    String traceId = UUID.randomUUID().toString().substring(0, 8);

    MethodSignature ms = (MethodSignature) pjp.getSignature();
    String classMethod = ms.getDeclaringType().getSimpleName() + "." + ms.getName();

    String user = currentUserSafe();

    // 处理入参（截断/脱敏）
    String argsStr = maskAndLimitArgs(pjp.getArgs(), 2000);

    log.info("[{}] → {} user={} args={}", traceId, classMethod, user, argsStr);

    Object result = null;
    int statusGuess = 200;
    try {
      result = pjp.proceed();

      // 如果是 ResponseEntity，拿出状态码
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

  // 兜底异常日志（即使未走到 Around 的 catch）
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

  // —— 工具：参数脱敏与截断 ——

  private String maskAndLimitArgs(Object[] args, int maxLen) {
    // 简单脱敏：Authorization 字段、password/secret 字段名
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
