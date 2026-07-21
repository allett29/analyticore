package com.analyticore.analyseservice.config;

import com.zaxxer.hikari.HikariDataSource;
import java.net.URI;
import java.net.URISyntaxException;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Capa de Infraestructura — Configuración del bus PostgreSQL.
 *
 * Variable externa Render: DATABASE_URL
 * Usado por: repository/JobRepository.java (Spring Data JPA → JDBC)
 * Tabla compartida con Python: 'jobs' (database/schema.sql)
 */
@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties props = new DataSourceProperties();
        props.setDriverClassName("org.postgresql.Driver");

        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            props.setUrl("jdbc:postgresql://localhost:5432/analyticore");
            props.setUsername(envOrDefault("DATABASE_USER", "analyticore"));
            props.setPassword(envOrDefault("DATABASE_PASSWORD", "analyticore"));
            return props;
        }

        ParsedDbUrl parsed = parseDatabaseUrl(databaseUrl);
        props.setUrl(parsed.jdbcUrl());
        props.setUsername(parsed.username());
        props.setPassword(parsed.password());
        return props;
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    private ParsedDbUrl parseDatabaseUrl(String databaseUrl) {
        if (databaseUrl.startsWith("jdbc:postgresql://")) {
            return new ParsedDbUrl(
                    databaseUrl,
                    envOrDefault("DATABASE_USER", "analyticore"),
                    envOrDefault("DATABASE_PASSWORD", "analyticore")
            );
        }

        String uriScheme = databaseUrl.startsWith("postgres://") ? "http://" : "http://";
        String normalized = databaseUrl
                .replaceFirst("^postgres://", uriScheme)
                .replaceFirst("^postgresql://", uriScheme);

        try {
            URI uri = new URI(normalized);
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String dbName = uri.getPath().replaceFirst("^/", "");

            String username = "";
            String password = "";
            String userInfo = uri.getUserInfo();
            if (userInfo != null) {
                int colon = userInfo.indexOf(':');
                if (colon >= 0) {
                    username = userInfo.substring(0, colon);
                    password = userInfo.substring(colon + 1);
                } else {
                    username = userInfo;
                }
            }

            String jdbcUrl = String.format(
                    "jdbc:postgresql://%s:%d/%s?sslmode=require",
                    host, port, dbName
            );

            return new ParsedDbUrl(jdbcUrl, username, password);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("DATABASE_URL inválida", e);
        }
    }

    private String envOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    private record ParsedDbUrl(String jdbcUrl, String username, String password) {}
}
