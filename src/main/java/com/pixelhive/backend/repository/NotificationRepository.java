package com.pixelhive.backend.repository;

import com.pixelhive.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Newest notifications first, for one user.
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
