package com.pixelhive.backend.controller;

import com.pixelhive.backend.dto.CreateNotificationRequest;
import com.pixelhive.backend.entity.Notification;
import com.pixelhive.backend.entity.User;
import com.pixelhive.backend.repository.NotificationRepository;
import com.pixelhive.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // GET /api/notifications?userId=1 - a user's notifications, newest first.
    @GetMapping
    public List<Notification> getForUser(@RequestParam Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // POST /api/notifications - create a notification.
    @PostMapping
    public Notification create(@RequestBody CreateNotificationRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No user with id " + request.userId()));
        Notification n = new Notification();
        n.setUser(user);
        n.setType(request.type());
        n.setMessage(request.message());
        return notificationRepository.save(n);
    }

    // POST /api/notifications/{id}/read - mark one as read.
    @PostMapping("/{id}/read")
    public Notification markRead(@PathVariable Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No notification with id " + id));
        n.setRead(true);
        return notificationRepository.save(n);
    }
}
