package com.fooddelivery.restaurant_service.security;

import lombok.AllArgsConstructor;
import lombok.Data;

// Carries the extra claims (numeric user id + role) the JWT holds
// beyond the email subject, so services can do ownership/role checks.
@Data
@AllArgsConstructor
public class JwtUserDetails {
    private Long userId;
    private String role;
}
