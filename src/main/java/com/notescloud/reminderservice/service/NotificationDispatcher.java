package com.notescloud.reminderservice.service;

import com.notescloud.reminderservice.client.GatewayNotificationClient;
import com.notescloud.reminderservice.entity.Reminder;
import com.notescloud.reminderservice.view.GatewayNotificationPushRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private static final String NOTIFICATION_TYPE = "REMINDER_FIRED";

    private final GatewayNotificationClient gatewayNotificationClient;

    public void dispatchToUser(Reminder reminder, UUID notificationId) {
        if (!reminder.isNotifyInApp()) {
            log.debug("Reminder {} has notifyInApp disabled, skipping", reminder.getId());
            return;
        }

        GatewayNotificationPushRequest request = new GatewayNotificationPushRequest(
            reminder.getUserId(),
            notificationId,
            reminder.getId(),
            reminder.getHeading(),
            reminder.getDescription(),
            reminder.getPriority(),
            Instant.now(),
            NOTIFICATION_TYPE
        );

        gatewayNotificationClient.pushNotification(request);

        log.info("Requested gateway push for notification {} to user {} for reminder {}",
            notificationId,
            reminder.getUserId(),
            reminder.getId());
    }
}