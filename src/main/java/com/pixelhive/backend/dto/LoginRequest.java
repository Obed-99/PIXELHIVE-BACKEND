package com.pixelhive.backend.dto;

// What a user sends to log in.
public record LoginRequest(String email, String password) {
}
