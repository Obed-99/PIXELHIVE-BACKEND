package com.pixelhive.backend.controller;

import com.pixelhive.backend.dto.SendMessageRequest;
import com.pixelhive.backend.entity.Message;
import com.pixelhive.backend.entity.Project;
import com.pixelhive.backend.entity.User;
import com.pixelhive.backend.repository.MessageRepository;
import com.pixelhive.backend.repository.ProjectRepository;
import com.pixelhive.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public MessageController(MessageRepository messageRepository,
                             ProjectRepository projectRepository,
                             UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // GET /api/messages?projectId=1 - the conversation for a project.
    @GetMapping
    public List<Message> getMessages(@RequestParam Long projectId) {
        return messageRepository.findByProjectIdOrderByCreatedAtAsc(projectId);
    }

    // POST /api/messages - send a message.
    @PostMapping
    public Message sendMessage(@RequestBody SendMessageRequest request) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No project with id " + request.projectId()));
        User sender = userRepository.findById(request.senderId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No user with id " + request.senderId()));

        Message message = new Message();
        message.setProject(project);
        message.setSender(sender);
        message.setContent(request.content());
        return messageRepository.save(message);
    }
}
