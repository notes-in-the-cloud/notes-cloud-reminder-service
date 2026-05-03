package com.notescloud.reminderservice.view;

import com.notescloud.reminderservice.enums.Priority;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class NotificationPayload {
    private UUID notificationId;
    private UUID reminderId;
    private String heading;
    private String message;
    private Priority priority;
    private Instant firedAt;
    private String type; // "REMINDER_FIRED" - за бъдеща extensibility
}
