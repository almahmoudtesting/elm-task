package com.example.demo.dto;

import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserResponse {
    private final Long id;
    private final String username;
    private final String email;

    public UserResponse(User currentUser) {
        this.id = currentUser.getId();
        this.username = currentUser.getUsername();
        this.email = currentUser.getEmail();
    }
}