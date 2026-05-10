package com.notescloud.reminderservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway")
public record GatewayProperties(
    String baseUrl,
    String internalToken
) {
}