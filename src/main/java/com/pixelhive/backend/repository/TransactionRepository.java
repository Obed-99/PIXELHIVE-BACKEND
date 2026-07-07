package com.pixelhive.backend.repository;

import com.pixelhive.backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByProjectId(Long projectId);
}
