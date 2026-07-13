package com.pixelhive.backend.dto;

import com.pixelhive.backend.entity.User;

// What a successful login returns: the JWT plus the user it belongs to.
public record LoginResponse(String token, User user) {
}
