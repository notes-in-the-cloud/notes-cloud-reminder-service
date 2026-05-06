package com.notescloud.reminderservice.view;

import com.notescloud.reminderservice.enums.Priority;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class NotificationView {
    private UUID id;
    private UUID userId;
    private UUID reminderId;
    private String heading;
    private String message;
    private Priority priority;
    private boolean read;
    private Instant readAt;
    private Instant firedAt;
}