package com.pixelhive.backend.repository;

import com.pixelhive.backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // "find every project whose creator has this id" - Spring writes the SQL.
    List<Project> findByCreatorId(Long creatorId);
}
