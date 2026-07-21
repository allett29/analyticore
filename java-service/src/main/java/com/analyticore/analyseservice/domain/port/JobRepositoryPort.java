package com.analyticore.analyseservice.domain.port;

import com.analyticore.analyseservice.domain.Job;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de dominio — contrato de persistencia de jobs (inversión de dependencias).
 * La capa de aplicación depende de esta interfaz, no de JPA ni PostgreSQL.
 */
public interface JobRepositoryPort {

    Optional<Job> findById(UUID jobId);

    Job save(Job job);

    Optional<Job> findTopByOrderByCreatedAtDesc();
}
