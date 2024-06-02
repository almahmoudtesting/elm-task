package com.example.demo.aspect;


import com.example.demo.configs.UserRateLimiter;
import com.example.demo.exceptions.RateLimitExceededException;
import com.example.demo.service.JwtService;

import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Component
public class RateLimitingAspect {

    private final JwtService jwtService;
    private final UserRateLimiter rateLimiter;
    private final RateLimiterRegistry rateLimiterRegistry;


    public RateLimitingAspect(JwtService jwtService, UserRateLimiter rateLimiter, RateLimiterRegistry rateLimiterRegistry) {
        this.jwtService = jwtService;
        this.rateLimiter = rateLimiter;
        this.rateLimiterRegistry = rateLimiterRegistry;
    }


    @Around("@annotation(rateLimited)")
    public Object applyRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new Exception("Invalid Authorization header");
        }
        String jwt = authHeader.substring(7);

        Claims claims = jwtService.extractAllClaims(jwt);


        String userEmail = claims.get("email", String.class);

        if (!rateLimiter.allowRequest(userEmail)) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }

        return joinPoint.proceed();
    }
}