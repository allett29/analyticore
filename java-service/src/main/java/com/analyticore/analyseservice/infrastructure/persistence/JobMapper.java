package com.analyticore.analyseservice.infrastructure.persistence;

import com.analyticore.analyseservice.domain.Job;

/**
 * Mapeo entre entidad de dominio y entidad JPA.
 */
public final class JobMapper {

    private JobMapper() {}

    public static Job toDomain(JobEntity entity) {
        Job job = new Job();
        job.setId(entity.getId());
        job.setText(entity.getText());
        job.setStatus(entity.getStatus());
        job.setSentiment(entity.getSentiment());
        job.setScore(entity.getScore());
        job.setKeywords(entity.getKeywords());
        job.setCreatedAt(entity.getCreatedAt());
        job.setUpdatedAt(entity.getUpdatedAt());
        return job;
    }

    public static void updateEntity(JobEntity entity, Job job) {
        entity.setId(job.getId());
        entity.setText(job.getText());
        entity.setStatus(job.getStatus());
        entity.setSentiment(job.getSentiment());
        entity.setScore(job.getScore());
        entity.setKeywords(job.getKeywords());
        entity.setCreatedAt(job.getCreatedAt());
        entity.setUpdatedAt(job.getUpdatedAt());
    }
}
