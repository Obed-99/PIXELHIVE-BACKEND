package com.pixelhive.backend.controller;

import com.pixelhive.backend.dto.LoginRequest;
import com.pixelhive.backend.entity.User;
import com.pixelhive.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // POST /api/auth/login - check an email + password.
    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {
        // Find the user by email. If none, fail with the SAME message as a
        // wrong password (never reveal which one was wrong - that's a security best practice).
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        // matches() hashes the incoming password and compares it to the stored
        // hash. You can never un-hash - you can only check for a match.
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // Success: return the user (the password hash is hidden by @JsonIgnore).
        return user;
    }
}
