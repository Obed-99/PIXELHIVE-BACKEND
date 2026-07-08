package com.pixelhive.backend.service;

import com.pixelhive.backend.entity.Contract;
import com.pixelhive.backend.entity.Project;
import com.pixelhive.backend.repository.ContractRepository;
import com.pixelhive.backend.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final ProjectRepository projectRepository;

    public ContractService(ContractRepository contractRepository, ProjectRepository projectRepository) {
        this.contractRepository = contractRepository;
        this.projectRepository = projectRepository;
    }

    // Generate (or regenerate) the contract for a project.
    // Uses a structured template for now - swap this body for a real
    // AI/LLM call later without changing anything else.
    public Contract generateForProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No project with id " + projectId));

        // Reuse the existing contract row if there is one, else make a new one.
        List<Contract> existing = contractRepository.findByProjectId(projectId);
        Contract contract = existing.isEmpty() ? new Contract() : existing.get(0);

        contract.setProject(project);
        contract.setContent(buildContractText(project));
        contract.setStatus("draft");
        contract.setSignedAt(null);

        return contractRepository.save(contract);
    }

    // Sign the contract: mark it signed and stamp the time.
    public Contract sign(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No contract with id " + contractId));
        contract.setStatus("signed");
        contract.setSignedAt(OffsetDateTime.now());
        return contractRepository.save(contract);
    }

    private String buildContractText(Project p) {
        String creator = p.getCreator() != null ? p.getCreator().getFullName() : "the Creator";
        String client = p.getClient() != null ? p.getClient().getFullName() : "the Client";
        String price = p.getPrice() != null ? p.getPrice().toPlainString() : "the agreed amount";
        String desc = p.getDescription() != null ? p.getDescription() : p.getTitle();

        return "LICENSING & SERVICE AGREEMENT\n\n"
                + "This agreement is made between " + creator + " (\"Creator\") and "
                + client + " (\"Client\") for the project \"" + p.getTitle() + "\".\n\n"
                + "1. Scope - The Creator will deliver: " + desc + ".\n"
                + "2. Fee - The Client agrees to pay GHS " + price + " in full.\n"
                + "3. Previews - Watermarked previews remain the Creator's property. "
                + "Full-resolution originals are released only after payment clears.\n"
                + "4. License - Upon full payment, the Creator grants the Client a personal-use "
                + "license to the final deliverables.\n"
                + "5. Delivery - Final files are delivered within 5 days of signing.\n"
                + "6. Ownership - The Creator retains copyright unless otherwise agreed in writing.\n\n"
                + "By signing, both parties accept these terms.";
    }
}
