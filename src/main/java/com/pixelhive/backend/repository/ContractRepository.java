package com.pixelhive.backend.repository;

import com.pixelhive.backend.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByProjectId(Long projectId);
}
