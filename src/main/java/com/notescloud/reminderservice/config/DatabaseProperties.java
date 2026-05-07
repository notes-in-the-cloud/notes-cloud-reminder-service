package com.notescloud.reminderservice.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.database")
public class DatabaseProperties {

    @NotBlank
    private String host = "postgres";

    @NotNull
    private Integer port = 5432;

    @NotBlank
    private String name;

    @NotBlank
    private String user;

    @NotBlank
    private String password;

    public String jdbcUrl() {
        return "jdbc:postgresql://%s:%d/%s".formatted(host, port, name);
    }
}
