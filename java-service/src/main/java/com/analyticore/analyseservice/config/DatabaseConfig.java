package com.analyticore.analyseservice.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuración externa de PostgreSQL.
 * Render provee DATABASE_URL en formato postgres:// — se convierte a jdbc:postgresql://
 * Docker Compose usa jdbc:postgresql:// directamente.
 */
@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties props = new DataSourceProperties();
        props.setDriverClassName("org.postgresql.Driver");
        props.setUrl(resolveDatabaseUrl());
        return props;
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    private String resolveDatabaseUrl() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl == null || databaseUrl.isBlank()) {
            return "jdbc:postgresql://localhost:5432/analyticore";
        }

        // Render: postgres://user:pass@host:port/db → jdbc:postgresql://...
        if (databaseUrl.startsWith("postgres://")) {
            return databaseUrl.replace("postgres://", "jdbc:postgresql://");
        }
        if (databaseUrl.startsWith("postgresql://")) {
            return databaseUrl.replace("postgresql://", "jdbc:postgresql://");
        }

        // Docker Compose: ya viene en formato jdbc:postgresql://
        return databaseUrl;
    }
}
