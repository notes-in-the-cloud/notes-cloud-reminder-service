package com.notescloud.reminderservice.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("prod")
@RequiredArgsConstructor
public class DataSourceConfig {

    private final DatabaseProperties databaseProperties;

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .driverClassName("org.postgresql.Driver")
                .url(databaseProperties.jdbcUrl())
                .username(databaseProperties.getUser())
                .password(databaseProperties.getPassword())
                .build();
    }
}
