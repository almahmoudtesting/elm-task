package com.example.demo.aspect;

import com.example.demo.aspect.RateLimited;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Aspect
@Component
public class RateLimitedAspect {
    private final RateLimiterRegistry rateLimiterRegistry;

    public RateLimitedAspect(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Around("@annotation(com.example.demo.aspect.RateLimited)")
    public Object rateLimitMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        RateLimited rateLimited = methodSignature.getMethod().getAnnotation(RateLimited.class);

        String rateLimiterKey = getRateLimiterKey(joinPoint);
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(rateLimiterKey, RateLimiterConfig.custom()
                .limitForPeriod(rateLimited.limitForPeriod())
                .limitRefreshPeriod(Duration.ofMinutes(rateLimited.limitRefreshPeriod()))
                .timeoutDuration(Duration.ofSeconds(60))
                .build());

        Supplier<Object> methodCall = () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };

        Supplier<Boolean> rateLimiterAcquire = rateLimiter::acquirePermission;

        CompletableFuture<Object> methodFuture = CompletableFuture.supplyAsync(methodCall);
        CompletableFuture<Boolean> rateLimiterFuture = CompletableFuture.supplyAsync(rateLimiterAcquire);

        return CompletableFuture.allOf(methodFuture, rateLimiterFuture)
                .thenApply(v -> {
                    if (rateLimiterFuture.join()) {
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(methodFuture.join());
                    } else {
                        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
                    }
                })
                .join();
    }

    private String getRateLimiterKey(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        StringBuilder keyBuilder = new StringBuilder(method.getDeclaringClass().getName() + "." + method.getName());

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            keyBuilder.append("-").append(arg.toString());
        }

        return keyBuilder.toString();
    }
}