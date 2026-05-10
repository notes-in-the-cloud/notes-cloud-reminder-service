package com.notescloud.reminderservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    DatabaseProperties.class,
    GatewayProperties.class
})
public class PropertiesConfig {
}