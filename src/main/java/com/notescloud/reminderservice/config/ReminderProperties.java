package com.notescloud.reminderservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reminder")
public record ReminderProperties(
    String timezone
) {
}