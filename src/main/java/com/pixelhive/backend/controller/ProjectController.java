package com.pixelhive.backend.controller;

import com.pixelhive.backend.dto.CreateProjectRequest;
import com.pixelhive.backend.dto.UpdateProjectRequest;
import com.pixelhive.backend.dto.UpdateStatusRequest;
import com.pixelhive.backend.entity.Project;
import com.pixelhive.backend.entity.User;
import com.pixelhive.backend.repository.ProjectRepository;
import com.pixelhive.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectController(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // GET /api/projects                -> every project
    // GET /api/projects?creatorId=1    -> only that creator's projects
    // GET /api/projects?clientId=2     -> only projects made for that client
    @GetMapping
    public List<Project> getProjects(@RequestParam(required = false) Long creatorId,
                                     @RequestParam(required = false) Long clientId) {
        if (creatorId != null) {
            return projectRepository.findByCreatorId(creatorId);
        }
        if (clientId != null) {
            return projectRepository.findByClientId(clientId);
        }
        return projectRepository.findAll();
    }

    // GET /api/projects/{id} - fetch ONE project by its id.
    // The {id} in the URL is captured into the 'id' parameter by @PathVariable.
    @GetMapping("/{id}")
    public Project getProject(@PathVariable Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No project with id " + id));
    }

    // POST /api/projects - create a project for a given creator.
    @PostMapping
    public Project createProject(@RequestBody CreateProjectRequest request) {
        User creator = userRepository.findById(request.creatorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No user found with creatorId " + request.creatorId()));

        Project project = new Project();
        project.setCreator(creator);

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

    // PATCH /api/projects/{id} - edit a project (e.g. the creator reprices it).
    @PatchMapping("/{id}")
    public Project updateProject(@PathVariable Long id, @RequestBody UpdateProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No project with id " + id));

        if (request.title() != null && !request.title().isBlank()) {
            project.setTitle(request.title());
        }
        if (request.description() != null) {
            project.setDescription(request.description());
        }
        if (request.price() != null) {
            if (request.price().signum() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price must be positive");
            }
            project.setPrice(request.price());
        }
        return projectRepository.save(project);
    }

    // PATCH /api/projects/{id}/status - move a project along its lifecycle.
    @PatchMapping("/{id}/status")
    public Project updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        // Guard: only allow the statuses your database CHECK constraint permits.
        Set<String> allowed = Set.of("draft", "active", "delivered", "completed", "cancelled");
        if (request.status() == null || !allowed.contains(request.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status must be one of " + allowed);
        }

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No project with id " + id));

        project.setStatus(request.status());
        return projectRepository.save(project);
    }
}
