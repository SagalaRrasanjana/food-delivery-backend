package com.fooddelivery.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.auth_service.dto.LoginRequest;
import com.fooddelivery.auth_service.dto.RegisterRequest;
import com.fooddelivery.auth_service.service.AuthService;

import lombok.RequiredArgsConstructor;

// @RestController tells Spring this class handles web requests and returns JSON/Text
@RestController
// @RequestMapping sets the base URL for all endpoints in this file
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // Injecting the service we built earlier
    private final AuthService authService;

    // @PostMapping means this method only triggers on HTTP POST requests to /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        
        // 1. Pass the incoming request to the Service layer
        String responseMessage = authService.register(request);
        
        // 2. Return an HTTP 200 OK status with the success message
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        
        // Calls the service, which returns the JWT string if successful
        String token = authService.login(request);
        
        return ResponseEntity.ok(token);
    }

    // Test
    @GetMapping("/secure-data")
    public ResponseEntity<String> getSecureData() {
        return ResponseEntity.ok("Test Succes -");
    }
}