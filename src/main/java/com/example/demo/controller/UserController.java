package com.example.demo.controller;


import com.example.demo.aspect.RateLimited;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import io.github.resilience4j.ratelimiter.operator.RateLimiterOperator;
import io.reactivex.Flowable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RateLimited(limitForPeriod = 20, limitRefreshPeriod = 60)
    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<Callable<ResponseEntity<UserResponse>>> authenticatedUser() {
        return ResponseEntity.ok(
                () -> {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication == null || authentication.getPrincipal() == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }

                    User currentUser = (User) authentication.getPrincipal();
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return ResponseEntity.ok(new UserResponse(currentUser));
                }
        );
    }

    @RateLimited(limitForPeriod = 20, limitRefreshPeriod = 60)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }
}