package com.analyticore.analyseservice.repository;

import com.analyticore.analyseservice.domain.Job;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Capa de Infraestructura — Adaptador JDBC/JPA hacia PostgreSQL (Render).
 *
 * BUS DE COMUNICACIÓN: JDBC → PostgreSQL tabla 'jobs'
 * URL de conexión: config/DatabaseConfig.java (variable DATABASE_URL en Render)
 *
 * Quién usa este repositorio:
 *   service/AnalysisService.java línea 57  → findById()  SELECT (leer texto)
 *   service/AnalysisService.java línea 68  → save()      UPDATE PROCESANDO
 *   service/AnalysisService.java línea 85  → save()      UPDATE COMPLETADO + resultados
 *
 * Python también accede a la misma tabla vía infrastructure/database.py (SQLAlchemy).
 */
@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

    /** Solo para panel visual de demo — no es flujo de negocio. */
    Optional<Job> findTopByOrderByCreatedAtDesc();
}
