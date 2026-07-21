package com.analyticore.analyseservice.infrastructure.persistence;

import com.analyticore.analyseservice.domain.Job;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA — opera sobre JobEntity (infraestructura).
 */
@Repository
interface JpaJobRepository extends JpaRepository<JobEntity, UUID> {

    java.util.Optional<JobEntity> findTopByOrderByCreatedAtDesc();
}
