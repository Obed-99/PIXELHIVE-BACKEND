package com.pixelhive.backend.dto;

// Inputs for a dynamic pricing suggestion.
public record PricingRequest(String resolution, Integer durationMinutes, String quality) {
}
