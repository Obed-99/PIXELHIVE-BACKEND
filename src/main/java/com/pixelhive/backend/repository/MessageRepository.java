package com.pixelhive.backend.repository;

import com.pixelhive.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // A project's chat, oldest first.
    List<Message> findByProjectIdOrderByCreatedAtAsc(Long projectId);
}
