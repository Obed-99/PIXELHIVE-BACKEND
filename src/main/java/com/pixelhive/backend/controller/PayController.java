package com.pixelhive.backend.controller;

import com.pixelhive.backend.dto.CreateTransactionRequest;
import com.pixelhive.backend.entity.Project;
import com.pixelhive.backend.entity.Transaction;
import com.pixelhive.backend.repository.ProjectRepository;
import com.pixelhive.backend.service.PaystackService;
import com.pixelhive.backend.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/pay")
public class PayController {

    public record InitPayRequest(Long projectId, String email) {}
    public record VerifyPayRequest(Long projectId, String reference) {}

    private final PaystackService paystackService;
    private final TransactionService transactionService;
    private final ProjectRepository projectRepository;

    public PayController(PaystackService paystackService, TransactionService transactionService,
                         ProjectRepository projectRepository) {
        this.paystackService = paystackService;
        this.transactionService = transactionService;
        this.projectRepository = projectRepository;
    }

    // POST /api/pay/initialize - start a Paystack checkout for a project.
    // Without a configured key it answers {demo:true} and the app uses demo payment.
    @PostMapping("/initialize")
    public Map<String, Object> initialize(@RequestBody InitPayRequest request) {
        if (!paystackService.isConfigured()) {
            return Map.of("demo", true);
        }
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No project with id " + request.projectId()));

        BigDecimal price = project.getPrice() == null ? BigDecimal.ZERO : project.getPrice();
        long pesewas = price.multiply(BigDecimal.valueOf(100)).longValue();
        String reference = "PXH-" + project.getId() + "-" + System.currentTimeMillis();

        Map<String, Object> res = paystackService.initialize(request.email(), pesewas, reference);
        Object data = res == null ? null : res.get("data");
        if (!(data instanceof Map<?, ?> d) || d.get("authorization_url") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Paystack did not return a checkout URL");
        }
        return Map.of(
                "demo", false,
                "authorizationUrl", String.valueOf(d.get("authorization_url")),
                "reference", reference);
    }

    // POST /api/pay/verify - confirm with Paystack, then unlock the files.
    @PostMapping("/verify")
    public Transaction verify(@RequestBody VerifyPayRequest request) {
        if (!paystackService.isConfigured()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paystack is not configured");
        }
        Map<String, Object> res = paystackService.verify(request.reference());
        Object data = res == null ? null : res.get("data");
        if (!(data instanceof Map<?, ?> d) || !"success".equals(d.get("status"))) {
            throw new ResponseStatusException(
                    HttpStatus.PAYMENT_REQUIRED, "Payment not completed yet - finish checkout first");
        }
        // Paystack reports the amount it actually charged, in pesewas.
        BigDecimal amount = new BigDecimal(String.valueOf(d.get("amount")))
                .divide(BigDecimal.valueOf(100));
        return transactionService.recordSuccessfulPayment(
                new CreateTransactionRequest(request.projectId(), amount, request.reference()));
    }
}
