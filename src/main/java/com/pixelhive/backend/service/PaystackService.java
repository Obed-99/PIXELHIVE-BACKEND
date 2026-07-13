package com.pixelhive.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

// Talks to the real Paystack API (test mode when a test secret key is set).
// If no key is configured, the app falls back to demo payments.
@Service
public class PaystackService {

    private final String secret;
    private final RestClient rest;

    public PaystackService(@Value("${pixelhive.paystack.secret}") String secret) {
        this.secret = secret;
        this.rest = RestClient.builder().baseUrl("https://api.paystack.co").build();
    }

    public boolean isConfigured() {
        return secret != null && !secret.isBlank();
    }

    // Starts a checkout; Paystack returns a hosted payment page URL.
    @SuppressWarnings("unchecked")
    public Map<String, Object> initialize(String email, long amountPesewas, String reference) {
        return rest.post()
                .uri("/transaction/initialize")
                .header("Authorization", "Bearer " + secret)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "email", email,
                        "amount", amountPesewas,
                        "currency", "GHS",
                        "reference", reference))
                .retrieve()
                .body(Map.class);
    }

    // Asks Paystack whether a reference was actually paid.
    @SuppressWarnings("unchecked")
    public Map<String, Object> verify(String reference) {
        return rest.get()
                .uri("/transaction/verify/{reference}", reference)
                .header("Authorization", "Bearer " + secret)
                .retrieve()
                .body(Map.class);
    }
}
