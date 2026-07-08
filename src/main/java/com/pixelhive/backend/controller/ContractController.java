package com.pixelhive.backend.controller;

import com.pixelhive.backend.dto.GenerateContractRequest;
import com.pixelhive.backend.entity.Contract;
import com.pixelhive.backend.repository.ContractRepository;
import com.pixelhive.backend.service.ContractService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService contractService;
    private final ContractRepository contractRepository;

    public ContractController(ContractService contractService, ContractRepository contractRepository) {
        this.contractService = contractService;
        this.contractRepository = contractRepository;
    }

    // GET /api/contracts?projectId=1 - the contract(s) for a project.
    @GetMapping
    public List<Contract> getContracts(@RequestParam(required = false) Long projectId) {
        if (projectId != null) {
            return contractRepository.findByProjectId(projectId);
        }
        return contractRepository.findAll();
    }

    // POST /api/contracts/generate - auto-generate a contract for a project.
    @PostMapping("/generate")
    public Contract generate(@RequestBody GenerateContractRequest request) {
        return contractService.generateForProject(request.projectId());
    }

    // POST /api/contracts/{id}/sign - digitally sign the contract.
    @PostMapping("/{id}/sign")
    public Contract sign(@PathVariable Long id) {
        return contractService.sign(id);
    }
}
