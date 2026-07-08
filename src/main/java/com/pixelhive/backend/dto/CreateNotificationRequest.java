package com.pixelhive.backend.dto;

// Create a notification for a user.
public record CreateNotificationRequest(Long userId, String type, String message) {
}
