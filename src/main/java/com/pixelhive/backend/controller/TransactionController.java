package com.pixelhive.backend.controller;

import com.pixelhive.backend.dto.CreateTransactionRequest;
import com.pixelhive.backend.entity.Transaction;
import com.pixelhive.backend.repository.TransactionRepository;
import com.pixelhive.backend.service.TransactionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionService transactionService,
                                 TransactionRepository transactionRepository) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
    }

    // GET /api/transactions - list every payment.
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // POST /api/transactions - record a successful payment.
    // The controller stays thin: it just hands the work to the service.
    @PostMapping
    public Transaction recordPayment(@RequestBody CreateTransactionRequest request) {
        return transactionService.recordSuccessfulPayment(request);
    }
}
