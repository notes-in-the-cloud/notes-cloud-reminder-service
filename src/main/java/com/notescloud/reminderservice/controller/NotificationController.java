package com.notescloud.reminderservice.controller;

import com.notescloud.reminderservice.service.NotificationService;
import com.notescloud.reminderservice.view.NotificationView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationView> getAllForUser(@RequestHeader("X-User-Id") UUID userId) {
        return notificationService.getAllForUser(userId);
    }

    @GetMapping("/unread")
    public List<NotificationView> getUnreadForUser(@RequestHeader("X-User-Id") UUID userId) {
        return notificationService.getUnreadForUser(userId);
    }

    @GetMapping("/unread/count")
    public long countUnread(@RequestHeader("X-User-Id") UUID userId) {
        return notificationService.countUnreadForUser(userId);
    }

    @PostMapping("/{id}/read")
    public NotificationView markAsRead(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        return notificationService.markAsRead(id, userId);
    }

    @PostMapping("/read-all")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Integer> markAllAsRead(@RequestHeader("X-User-Id") UUID userId) {
        int count = notificationService.markAllAsReadForUser(userId);
        return Map.of("markedAsRead", count);
    }
}