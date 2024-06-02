package com.example.demo.configs;

import com.example.demo.aspect.RateLimitingAspect;
import com.example.demo.service.JwtService;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitingConfiguration {


    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(60))
                .build();

        return RateLimiterRegistry.of(rateLimiterConfig);
    }

    @Bean
    public RateLimitingAspect rateLimitingAspect(RateLimiterRegistry rateLimiterRegistry,
                                                 UserRateLimiter userRateLimiter, JwtService jwtService) {
        return new RateLimitingAspect(jwtService, userRateLimiter, rateLimiterRegistry);
    }
}