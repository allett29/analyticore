package com.analyticore.analyseservice.infrastructure.persistence;

import com.analyticore.analyseservice.domain.Job;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Capa de Infraestructura — Adaptador JDBC/JPA hacia PostgreSQL (Render).
 *
 * BUS DE COMUNICACIÓN: JDBC → PostgreSQL tabla 'jobs'
 * URL de conexión: infrastructure/config/DatabaseConfig.java (variable DATABASE_URL)
 *
 * Python también accede a la misma tabla vía infrastructure/database.py (SQLAlchemy).
 */
@Repository
public class JobRepository {

    private final JpaJobRepository jpaJobRepository;

    public JobRepository(JpaJobRepository jpaJobRepository) {
        this.jpaJobRepository = jpaJobRepository;
    }

    public Optional<Job> findById(UUID jobId) {
        return jpaJobRepository.findById(jobId).map(JobMapper::toDomain);
    }

    public Job save(Job job) {
        JobEntity entity = jpaJobRepository.findById(job.getId()).orElse(new JobEntity());
        JobMapper.updateEntity(entity, job);
        return JobMapper.toDomain(jpaJobRepository.save(entity));
    }

    /** Solo para panel de monitoreo — lee desde PostgreSQL (stateless). */
    public Optional<Job> findTopByOrderByCreatedAtDesc() {
        return jpaJobRepository.findTopByOrderByCreatedAtDesc().map(JobMapper::toDomain);
    }
}
