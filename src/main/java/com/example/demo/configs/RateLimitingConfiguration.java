package com.example.demo.configs;

import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;

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
}