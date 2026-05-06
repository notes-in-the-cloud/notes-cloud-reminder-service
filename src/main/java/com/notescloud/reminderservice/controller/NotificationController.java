package com.notescloud.reminderservice.controller;

import com.notescloud.reminderservice.service.NotificationService;
import com.notescloud.reminderservice.view.NotificationView;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationView> getNotifications(
            @PathVariable UUID userId,
            @RequestParam(required = false) Boolean read) {

        if (Boolean.FALSE.equals(read)) {
            return notificationService.getUnreadForUser(userId);
        }
        return notificationService.getAllForUser(userId);
    }

    @GetMapping("/unread-count")
    public long countUnread(@PathVariable UUID userId) {
        return notificationService.countUnreadForUser(userId);
    }

    @PostMapping("/{id}/read")
    public NotificationView markAsRead(
            @PathVariable UUID userId,
            @PathVariable UUID id) {
        return notificationService.markAsRead(id, userId);
    }

    @PostMapping("/read-all")
    public void markAllAsRead(@PathVariable UUID userId) {
        notificationService.markAllAsReadForUser(userId);
    }

    @DeleteMapping
    public void deleteAll(@PathVariable UUID userId) {
        notificationService.deleteAllForUser(userId);
    }
}
