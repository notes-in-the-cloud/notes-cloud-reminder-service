package com.notescloud.reminderservice.view;

import com.notescloud.reminderservice.enums.Priority;
import com.notescloud.reminderservice.enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class ReminderView {
    private UUID id;
    private UUID userId;
    private String heading;
    private String description;
    private LocalDate reminderDate;
    private LocalTime reminderTime;
    private Priority priority;
    private Status status;
    private boolean notifyInApp;
    private boolean notifyEmail;
    private boolean notifyPush;
    private Instant createdAt;
    private Instant updatedAt;
}
