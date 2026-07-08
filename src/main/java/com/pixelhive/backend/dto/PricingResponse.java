package com.pixelhive.backend.dto;

// The suggested price the pricing engine returns.
public record PricingResponse(
        String resolution,
        Integer durationMinutes,
        String quality,
        String currency,
        long suggestedPrice
) {
}
