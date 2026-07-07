package com.pixelhive.backend.repository;

import com.pixelhive.backend.entity.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {

    // "find every media asset for this project id" - Spring writes the SQL.
    List<MediaAsset> findByProjectId(Long projectId);
}
