package com.pixelhive.backend.controller;

import com.pixelhive.backend.dto.PricingRequest;
import com.pixelhive.backend.dto.PricingResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    // POST /api/pricing/suggest - recommend a price from resolution,
    // duration and quality. Pure calculation, no database needed.
    @PostMapping("/suggest")
    public PricingResponse suggest(@RequestBody PricingRequest request) {
        double resFactor = switch (request.resolution() == null ? "" : request.resolution()) {
            case "4K" -> 1.6;
            case "8K" -> 2.4;
            default -> 1.0; // 1080p and anything else
        };
        double qualityFactor = switch (request.quality() == null ? "" : request.quality()) {
            case "high" -> 1.3;
            case "premium" -> 1.6;
            default -> 1.0; // standard
        };
        int minutes = request.durationMinutes() == null ? 1 : request.durationMinutes();

        // Base GHS 300 per minute, adjusted by resolution and quality, rounded to 50.
        long price = Math.round(300 * resFactor * minutes * qualityFactor / 50.0) * 50;

        return new PricingResponse(request.resolution(), minutes, request.quality(), "GHS", price);
    }
}
