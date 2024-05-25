package com.example.demo.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExternalServiceClient {

    @Value("${external.service.available}")
    private boolean isExternalServiceAvailable;

    public String getData(String vin) {


        // Implement logic to calls the external services
        if(!isExternalServiceAvailable) {
            throw new RuntimeException("External service is down");
        }

        return "Response from external service for VIN: " + vin;

    }
}
