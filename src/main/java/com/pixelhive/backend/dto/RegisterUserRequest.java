package com.pixelhive.backend.dto;

// The data a new user sends when they register.
// A 'record' is a compact class that just holds these values.
// Note the PLAIN 'password' - we hash it before saving; it never
// goes into the database as-is.
public record RegisterUserRequest(
        String email,
        String fullName,
        String password,
        String role
) {
}
