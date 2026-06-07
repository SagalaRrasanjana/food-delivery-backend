package com.fooddelivery.auth_service.dto;

import com.fooddelivery.auth_service.model.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RegisterRequest {
    
    // The name the user types into the registration form
    private String name;

    // The user's email address
    private String email;

    // The raw, unencrypted password sent from the frontend
    private String password;

    // The type of account being created 
    private Role role;
}
