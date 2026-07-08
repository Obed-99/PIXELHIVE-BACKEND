package com.pixelhive.backend.dto;

// Ask the server to generate a contract for this project.
public record GenerateContractRequest(Long projectId) {
}
