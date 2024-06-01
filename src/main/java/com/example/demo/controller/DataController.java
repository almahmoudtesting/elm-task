package com.example.demo.controller;

import com.example.demo.aspect.RateLimited;
import com.example.demo.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final DataService dataService;

    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @PreAuthorize("hasAnyRole('USER')")
    @RateLimited(limitForPeriod = 20, limitRefreshPeriod = 60)
    @PostMapping
    public ResponseEntity<String> getData(@RequestBody String vin, Authentication authentication) {
        String username = authentication.getName();
        String requestId = dataService.getData(vin, username);
        return ResponseEntity.ok(requestId);
    }
}
