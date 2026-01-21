package com.blog.multilanguage_platform.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    private final Environment env;

    public DatabaseConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() {
        String databaseUrl = env.getProperty("DATABASE_URL");
        
        // If DATABASE_URL not available, try SPRING_DATASOURCE_URL
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            databaseUrl = env.getProperty("SPRING_DATASOURCE_URL");
        }
        
        // Log what we're getting
        logger.info("Database URL found: {}", databaseUrl != null && !databaseUrl.isEmpty());
        
        // Transform Render's postgres:// URL to jdbc:postgresql://
        if (databaseUrl != null && databaseUrl.startsWith("postgres://")) {
            logger.info("Transforming postgres:// URL to jdbc:postgresql://");
            databaseUrl = databaseUrl.replace("postgres://", "jdbc:postgresql://");
        } else if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            logger.info("Adding jdbc: prefix to postgresql:// URL");
            databaseUrl = "jdbc:" + databaseUrl;
        }
        
        // Validate URL
        if (databaseUrl == null || databaseUrl.isEmpty() || !databaseUrl.startsWith("jdbc:")) {
            String msg = "Invalid database URL. Set DATABASE_URL or SPRING_DATASOURCE_URL environment variable";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }

        logger.info("Using database URL: {}", databaseUrl.replaceAll("(://.*:).*(@)", "$1***$2"));

        String username = env.getProperty("SPRING_DATASOURCE_USERNAME");
        String password = env.getProperty("SPRING_DATASOURCE_PASSWORD");

        if (username == null || username.isEmpty()) {
            String msg = "SPRING_DATASOURCE_USERNAME environment variable is required";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }

        if (password == null || password.isEmpty()) {
            String msg = "SPRING_DATASOURCE_PASSWORD environment variable is required";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }

        logger.info("Creating DataSource with username: {}", username);

        return DataSourceBuilder.create()
                .url(databaseUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
