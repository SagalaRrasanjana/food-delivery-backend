package com.fooddelivery.auth_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fooddelivery.auth_service.dto.LoginRequest;
import com.fooddelivery.auth_service.dto.RegisterRequest;
import com.fooddelivery.auth_service.model.User;
import com.fooddelivery.auth_service.repository.UserRepository;
import com.fooddelivery.auth_service.security.jwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    
    private final jwtUtil jwtUtil;
    public String register(RegisterRequest request){
        // 1. Validation: Check if the email already exists in PostgreSQL
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        // 2. Mapping: Convert the incoming DTO into our database User Entity
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
             // 3. Security: Hash the raw password before it  touches the database
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();  
                
                // 4. Persistence: Save the secure User entity to the auth_db
                 userRepository.save(user);

                // 5. Response: Return a simple confirmation message
                return "User registered successfully!";
    }

    public String login(LoginRequest request) {
        // 1. Find the user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // 2. Check if the provided password matches the hashed password in the database
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }

        // 3. Generate and return the JWT
        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }
}