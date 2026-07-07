package com.pixelhive.backend.service;

import com.pixelhive.backend.dto.CreateTransactionRequest;
import com.pixelhive.backend.entity.MediaAsset;
import com.pixelhive.backend.entity.Project;
import com.pixelhive.backend.entity.Transaction;
import com.pixelhive.backend.repository.MediaAssetRepository;
import com.pixelhive.backend.repository.ProjectRepository;
import com.pixelhive.backend.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.util.List;

// @Service = a class that holds business logic, separate from the web layer.
// Controllers stay thin and just call methods here.
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final MediaAssetRepository mediaAssetRepository;
    private final ProjectRepository projectRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              MediaAssetRepository mediaAssetRepository,
                              ProjectRepository projectRepository) {
        this.transactionRepository = transactionRepository;
        this.mediaAssetRepository = mediaAssetRepository;
        this.projectRepository = projectRepository;
    }

    // PixelHive's core rule: a successful payment UNLOCKS the project's media.
    // @Transactional = every database change in here commits together, or if
    // anything fails, they all roll back. No half-finished payments.
    @Transactional
    public Transaction recordSuccessfulPayment(CreateTransactionRequest request) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No project with id " + request.projectId()));

        // 1) Save the payment as successful.
        Transaction tx = new Transaction();
        tx.setProject(project);
        tx.setAmount(request.amount());
        tx.setPaystackRef(request.paystackRef());
        tx.setStatus("success");
        tx.setPaidAt(OffsetDateTime.now());
        Transaction saved = transactionRepository.save(tx);

        // 2) Release every media file on this project (uploaded -> released).
        List<MediaAsset> media = mediaAssetRepository.findByProjectId(project.getId());
        for (MediaAsset asset : media) {
            asset.setStatus("released");
        }
        mediaAssetRepository.saveAll(media);

        return saved;
    }
}
