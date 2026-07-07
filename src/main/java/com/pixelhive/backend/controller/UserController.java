package com.pixelhive.backend.controller;

import com.pixelhive.backend.dto.RegisterUserRequest;
import com.pixelhive.backend.entity.User;
import com.pixelhive.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Spring hands us both the repository and the password hasher.
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GET /api/users - list every user.
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // POST /api/users - register a new user.
    // @RequestBody turns the incoming JSON into a RegisterUserRequest.
    @PostMapping
    public User createUser(@RequestBody RegisterUserRequest request) {
        User user = new User();
        user.setEmail(request.email());
        user.setFullName(request.fullName());

        // NEVER store the raw password - hash it first.
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        // Use the role they sent, or fall back to 'client'.
        if (request.role() != null && !request.role().isBlank()) {
            user.setRole(request.role());
        }

        // save() INSERTs the row and returns it (now with its generated id).
        return userRepository.save(user);
    }
}
