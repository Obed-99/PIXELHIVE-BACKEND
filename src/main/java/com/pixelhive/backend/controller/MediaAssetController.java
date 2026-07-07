package com.pixelhive.backend.controller;

import com.pixelhive.backend.dto.CreateMediaAssetRequest;
import com.pixelhive.backend.entity.MediaAsset;
import com.pixelhive.backend.entity.Project;
import com.pixelhive.backend.repository.MediaAssetRepository;
import com.pixelhive.backend.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaAssetController {

    private final MediaAssetRepository mediaAssetRepository;
    private final ProjectRepository projectRepository;

    public MediaAssetController(MediaAssetRepository mediaAssetRepository, ProjectRepository projectRepository) {
        this.mediaAssetRepository = mediaAssetRepository;
        this.projectRepository = projectRepository;
    }

    // GET /api/media            -> all media
    // GET /api/media?projectId=1 -> only that project's media
    // @RequestParam reads the value after the "?" in the URL. required=false
    // means the parameter is optional.
    @GetMapping
    public List<MediaAsset> getMedia(@RequestParam(required = false) Long projectId) {
        if (projectId != null) {
            return mediaAssetRepository.findByProjectId(projectId);
        }
        return mediaAssetRepository.findAll();
    }

    // POST /api/media - register an uploaded file against a project.
    @PostMapping
    public MediaAsset createMedia(@RequestBody CreateMediaAssetRequest request) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No project with id " + request.projectId()));

        MediaAsset asset = new MediaAsset();
        asset.setProject(project);
        asset.setFileName(request.fileName());
        asset.setS3KeyOriginal(request.s3KeyOriginal());
        asset.setS3KeyPreview(request.s3KeyPreview());
        asset.setFileSize(request.fileSize());

        return mediaAssetRepository.save(asset);
    }
}
