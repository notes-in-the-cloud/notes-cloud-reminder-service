package com.notescloud.reminderservice.service;

import com.notescloud.reminderservice.entity.Reminder;
import com.notescloud.reminderservice.view.NotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private static final String NOTIFICATION_DESTINATION_PREFIX = "/topic/notifications/";

    private final SimpMessagingTemplate messagingTemplate;

    public void dispatchToUser(Reminder reminder, UUID notificationId) {
        if (!reminder.isNotifyInApp()) {
            log.debug("Reminder {} has notifyInApp disabled, skipping", reminder.getId());
            return;
        }

        NotificationPayload payload = NotificationPayload.builder()
                .notificationId(notificationId)        // NEW
                .reminderId(reminder.getId())
                .heading(reminder.getHeading())
                .message(reminder.getDescription())
                .priority(reminder.getPriority())
                .firedAt(Instant.now())
                .type("REMINDER_FIRED")
                .build();

        String userId = reminder.getUserId().toString();

        messagingTemplate.convertAndSend(NOTIFICATION_DESTINATION_PREFIX + userId, payload);

        log.info("Pushed notification {} to user {} for reminder {}",
                notificationId, userId, reminder.getId());
    }

}
