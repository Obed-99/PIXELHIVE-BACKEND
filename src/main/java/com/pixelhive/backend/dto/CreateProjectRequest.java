package com.pixelhive.backend.dto;

import java.math.BigDecimal;

// What the client sends to create a project. We take the creator's/client's
// id (a number), then look up the real User objects on the server.
public record CreateProjectRequest(
        Long creatorId,
        Long clientId,
        String title,
        String description,
        BigDecimal price
) {
}
