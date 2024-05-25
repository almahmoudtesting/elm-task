package com.example.demo.service;

import com.example.demo.entity.RequestLog;
import com.example.demo.repository.RequestLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class LogService {

    private final RequestLogRepository requestLogRepository;

    @Autowired
    public LogService(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String logRequest(String vin, String username,
                             boolean externalServiceSuccess, Optional<String> externalServiceResponse, String errorMessage) {

        RequestLog requestLog = new RequestLog();
        requestLog.setVin(vin);
        requestLog.setUsername(username);
        requestLog.setTimestamp(LocalDateTime.now());
        requestLog.setExternalServiceSuccess(externalServiceSuccess);
        externalServiceResponse.ifPresent(requestLog::setExternalServiceResponse);
        requestLog.setErrorMessage(errorMessage);
        requestLog.setRequestId(UUID.randomUUID().toString());
        return requestLogRepository.save(requestLog).getRequestId();
    }
}
