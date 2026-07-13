package com.pixelhive.backend.dto;

// What the client sends to register an uploaded media file against a project.
public record CreateMediaAssetRequest(
        Long projectId,
        String fileName,
        String s3KeyOriginal,
        String s3KeyPreview,
        Long fileSize,
        String previewData
) {
}
