package com.notescloud.reminderservice.service;

import com.notescloud.reminderservice.entity.Notification;
import com.notescloud.reminderservice.entity.Reminder;
import com.notescloud.reminderservice.exception.NotificationNotFoundException;
import com.notescloud.reminderservice.repository.NotificationRepository;
import com.notescloud.reminderservice.view.NotificationView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ConversionService conversionService;

    /**
     * Creates a notification record from a fired reminder.
     * Called internally by the scheduler when firing reminders.
     */
    @Transactional
    public Notification createFromReminder(Reminder reminder) {
        Notification notification = new Notification();
        notification.setUserId(reminder.getUserId());
        notification.setReminderId(reminder.getId());
        notification.setHeading(reminder.getHeading());
        notification.setMessage(reminder.getDescription());
        notification.setPriority(reminder.getPriority());
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationView> getAllForUser(UUID userId) {
        return notificationRepository.findByUserIdOrderByFiredAtDesc(userId)
                .stream()
                .map(n -> conversionService.convert(n, NotificationView.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationView> getUnreadForUser(UUID userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByFiredAtDesc(userId)
                .stream()
                .map(n -> conversionService.convert(n, NotificationView.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public long countUnreadForUser(UUID userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public NotificationView markAsRead(UUID id, UUID userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        if (!notification.getUserId().equals(userId)) {
            throw new NotificationNotFoundException(id);
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(Instant.now());
            notification = notificationRepository.save(notification);
        }

        return conversionService.convert(notification, NotificationView.class);
    }

    @Transactional
    public int markAllAsReadForUser(UUID userId) {
        int count = notificationRepository.markAllAsReadForUser(userId, Instant.now());
        log.info("Marked {} notifications as read for user {}", count, userId);
        return count;
    }
}