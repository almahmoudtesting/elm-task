package com.example.demo.configs;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class UserRateLimiter {
    private final Map<String, RateLimiterImpl> rateLimiters;

    public UserRateLimiter() {
        this.rateLimiters = new ConcurrentHashMap<>();
    }

    public boolean allowRequest(String userEmail) {
        RateLimiterImpl rateLimiter = rateLimiters.computeIfAbsent(userEmail, k -> new RateLimiterImpl(10, TimeUnit.MINUTES, 100));
        return rateLimiter.tryAcquire();
    }

    private static class RateLimiterImpl {
        private final long duration;
        private final TimeUnit timeUnit;
        private final int limit;
        private final Map<Long, Integer> requestCounts;

        public RateLimiterImpl(long duration, TimeUnit timeUnit, int limit) {
            this.duration = duration;
            this.timeUnit = timeUnit;
            this.limit = limit;
            this.requestCounts = new ConcurrentHashMap<>();
        }

        public boolean tryAcquire() {
            long currentTimestamp = System.currentTimeMillis();
            long durationInMillis = timeUnit.toMillis(duration);
            long windowStartTimestamp = currentTimestamp - durationInMillis;

            int count = calculateRequestCount(windowStartTimestamp, currentTimestamp);
            if (count >= limit) {
                return false;
            }

            incrementRequestCount(currentTimestamp);
            return true;
        }

        private int calculateRequestCount(long windowStartTimestamp, long currentTimestamp) {
            int count = 0;
            for (long timestamp = windowStartTimestamp; timestamp <= currentTimestamp; timestamp++) {
                Integer requestCount = requestCounts.get(timestamp);
                if (requestCount != null) {
                    count += requestCount;
                }
            }
            return count;
        }

        private void incrementRequestCount(long currentTimestamp) {
            requestCounts.compute(currentTimestamp, (key, value) -> value != null ? value + 1 : 1);
        }
    }
}