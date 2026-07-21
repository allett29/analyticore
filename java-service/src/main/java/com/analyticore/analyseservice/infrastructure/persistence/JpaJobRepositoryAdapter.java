package com.analyticore.analyseservice.infrastructure.persistence;

import com.analyticore.analyseservice.domain.Job;
import com.analyticore.analyseservice.domain.port.JobRepositoryPort;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de infraestructura — implementa JobRepositoryPort con JPA/PostgreSQL.
 */
@Repository
public class JpaJobRepositoryAdapter implements JobRepositoryPort {

    private final JpaJobRepository jpaJobRepository;

    public JpaJobRepositoryAdapter(JpaJobRepository jpaJobRepository) {
        this.jpaJobRepository = jpaJobRepository;
    }

    @Override
    public Optional<Job> findById(UUID jobId) {
        return jpaJobRepository.findById(jobId).map(JobMapper::toDomain);
    }

    @Override
    public Job save(Job job) {
        JobEntity entity = jpaJobRepository.findById(job.getId()).orElse(new JobEntity());
        JobMapper.updateEntity(entity, job);
        return JobMapper.toDomain(jpaJobRepository.save(entity));
    }

    @Override
    public Optional<Job> findTopByOrderByCreatedAtDesc() {
        return jpaJobRepository.findTopByOrderByCreatedAtDesc().map(JobMapper::toDomain);
    }
}
