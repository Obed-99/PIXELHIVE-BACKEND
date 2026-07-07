package com.pixelhive.backend.controller;

import com.pixelhive.backend.dto.CreateProjectRequest;
import com.pixelhive.backend.entity.Project;
import com.pixelhive.backend.entity.User;
import com.pixelhive.backend.repository.ProjectRepository;
import com.pixelhive.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectController(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // GET /api/projects - list every project.
    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // POST /api/projects - create a project for a given creator.
    @PostMapping
    public Project createProject(@RequestBody CreateProjectRequest request) {
        // Look up the creator User by id; reject the request if they don't exist.
        User creator = userRepository.findById(request.creatorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No user found with creatorId " + request.creatorId()));

        Project project = new Project();
        project.setCreator(creator);

        // A client is optional. If one was given, look them up too.
        if (request.clientId() != null) {
            User client = userRepository.findById(request.clientId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "No user found with clientId " + request.clientId()));
            project.setClient(client);
        }

        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setPrice(request.price());

        return projectRepository.save(project);
    }
}
