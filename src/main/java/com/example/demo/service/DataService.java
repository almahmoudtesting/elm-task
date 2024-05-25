package com.example.demo.service;


import com.example.demo.integration.ExternalServiceClient;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;


@Service
public class DataService {

    private final ExternalServiceClient externalServiceClient;

    private final LogService logService;

    @Autowired
    public DataService(ExternalServiceClient externalServiceClient, LogService logService) {
        this.externalServiceClient = externalServiceClient;
        this.logService = logService;
    }

    public String getData(String vin, String username) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(1))
                .retryExceptions(Exception.class)
                .build();

        Retry retry = Retry.of("externalServiceRetry", config);

        Supplier<String> retryableSupplier = Retry.decorateSupplier(retry, () -> externalServiceClient.getData(vin));

        try {
            String response = retryableSupplier.get();
            return logService.logRequest(vin, username, true,
                    Optional.ofNullable(response), null);
        } catch (Exception e) {
            return logService.logRequest(vin, username, false,
                    Optional.empty(), e.getMessage());
        }
    }

}
