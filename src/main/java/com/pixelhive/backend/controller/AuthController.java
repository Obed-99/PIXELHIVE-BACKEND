package com.pixelhive.backend.controller;

import com.pixelhive.backend.dto.LoginRequest;
import com.pixelhive.backend.dto.LoginResponse;
import com.pixelhive.backend.entity.User;
import com.pixelhive.backend.repository.UserRepository;
import com.pixelhive.backend.service.JwtService;
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
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // POST /api/auth/login - check an email + password, return a JWT.
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
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

        // Success: issue a signed token that expires in 24 hours. Every other
        // API call must present it in the Authorization header.
        String token = jwtService.createToken(user.getId(), user.getEmail(), user.getRole());
        return new LoginResponse(token, user);
    }
}
