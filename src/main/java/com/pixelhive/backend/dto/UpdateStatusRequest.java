package com.pixelhive.backend.dto;

// The body sent when changing a project's status, e.g. {"status":"active"}.
public record UpdateStatusRequest(String status) {
}
