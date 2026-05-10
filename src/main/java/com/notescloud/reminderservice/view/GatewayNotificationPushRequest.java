package com.notescloud.reminderservice.view;

import com.notescloud.reminderservice.enums.Priority;

import java.time.Instant;
import java.util.UUID;

public record GatewayNotificationPushRequest(
    UUID userId,
    UUID notificationId,
    UUID reminderId,
    String heading,
    String message,
    Priority priority,
    Instant firedAt,
    String type
) {
}