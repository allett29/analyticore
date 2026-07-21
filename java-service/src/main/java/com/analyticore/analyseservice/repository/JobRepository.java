package com.analyticore.analyseservice.repository;

import com.analyticore.analyseservice.domain.Job;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Capa de Infraestructura: acceso a PostgreSQL para la entidad Job.
 */
@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
}
