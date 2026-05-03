package com.notescloud.reminderservice.repository;

import com.notescloud.reminderservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserIdOrderByFiredAtDesc(UUID userId);

    List<Notification> findByUserIdAndReadFalseOrderByFiredAtDesc(UUID userId);

    long countByUserIdAndReadFalse(UUID userId);

    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.read = true, n.readAt = :readAt
            WHERE n.userId = :userId AND n.read = false
            """)
    int markAllAsReadForUser(@Param("userId") UUID userId, @Param("readAt") Instant readAt);
}