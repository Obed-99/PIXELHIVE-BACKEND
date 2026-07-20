package com.pixelhive.backend.dto;

import java.math.BigDecimal;

// Fields a creator may change on an existing project; nulls are left as-is.
public record UpdateProjectRequest(
        String title,
        String description,
        BigDecimal price
) {
}
