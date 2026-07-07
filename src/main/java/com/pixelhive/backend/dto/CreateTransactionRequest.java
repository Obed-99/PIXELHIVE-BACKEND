package com.pixelhive.backend.dto;

import java.math.BigDecimal;

// What the client sends when a payment has gone through on Paystack.
public record CreateTransactionRequest(
        Long projectId,
        BigDecimal amount,
        String paystackRef
) {
}
