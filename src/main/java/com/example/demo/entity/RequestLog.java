package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "request_logs")
public class RequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String vin;
    private String requestId;
    private String username;
    private LocalDateTime timestamp;
    private boolean externalServiceSuccess;
    private String externalServiceResponse;
    private String errorMessage;
}