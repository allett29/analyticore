package com.analyticore.analyseservice.domain;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Capa de Dominio — Entidad Job pura (sin anotaciones JPA).
 *
 * Tabla compartida: PostgreSQL 'jobs' (database/schema.sql)
 * Persistencia: infrastructure/persistence/JpaJobRepositoryAdapter.java
 */
public class Job {

    private UUID id;
    private String text;
    private JobStatus status;
    private String sentiment;
    private Double score;
    private String keywords;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
