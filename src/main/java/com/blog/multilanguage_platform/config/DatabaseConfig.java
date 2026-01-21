package com.blog.multilanguage_platform.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    private final Environment env;

    public DatabaseConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() {
        String databaseUrl = env.getProperty("SPRING_DATASOURCE_URL");
        
        // Transform Render's postgres:// URL to jdbc:postgresql://
        if (databaseUrl != null && databaseUrl.startsWith("postgres://")) {
            databaseUrl = databaseUrl.replace("postgres://", "jdbc:postgresql://");
        }
        
        // If no URL is provided, use a default (should not happen on Render)
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            throw new IllegalStateException("SPRING_DATASOURCE_URL environment variable is required");
        }

        String username = env.getProperty("SPRING_DATASOURCE_USERNAME");
        String password = env.getProperty("SPRING_DATASOURCE_PASSWORD");

        return DataSourceBuilder.create()
                .url(databaseUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
