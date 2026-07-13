package com.pixelhive.backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "media_assets")
public class MediaAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each media file belongs to one project (the foreign key project_id).
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    // Where the real high-res file lives in S3.
    @Column(name = "s3_key_original", nullable = false)
    private String s3KeyOriginal;

    // Where the watermarked preview lives (may be empty until generated).
    @Column(name = "s3_key_preview")
    private String s3KeyPreview;

    @Column(name = "file_size")
    private Long fileSize;

    // Base64 data-URL of the uploaded image (demo storage - swap for S3 later).
    @Column(name = "preview_data", columnDefinition = "text")
    private String previewData;

    // uploaded -> watermarked -> released (unlocked after payment).
    @Column(nullable = false)
    private String status = "uploaded";

    // Analytics counters.
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(name = "download_count", nullable = false)
    private int downloadCount = 0;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public MediaAsset() {
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getS3KeyOriginal() {
        return s3KeyOriginal;
    }

    public void setS3KeyOriginal(String s3KeyOriginal) {
        this.s3KeyOriginal = s3KeyOriginal;
    }

    public String getS3KeyPreview() {
        return s3KeyPreview;
    }

    public void setS3KeyPreview(String s3KeyPreview) {
        this.s3KeyPreview = s3KeyPreview;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getPreviewData() {
        return previewData;
    }

    public void setPreviewData(String previewData) {
        this.previewData = previewData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
