package com.pixelhive.backend.dto;

// Send a chat message in a project's conversation.
public record SendMessageRequest(Long projectId, Long senderId, String content) {
}
