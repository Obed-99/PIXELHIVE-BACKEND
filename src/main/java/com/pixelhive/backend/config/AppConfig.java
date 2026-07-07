package com.pixelhive.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// @Configuration marks a class where we define shared, reusable objects ("beans").
@Configuration
public class AppConfig {

    // This bean securely turns a plain password into a BCrypt hash.
    // Anywhere in the app that asks for a PasswordEncoder gets this one.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
