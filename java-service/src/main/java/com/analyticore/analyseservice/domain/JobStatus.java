package com.analyticore.analyseservice.domain;

/**
 * Capa de Dominio — Estados del ciclo de vida de un job.
 *
 * Sincronizado con python-service/domain/models.py JobStatus
 * y con la columna 'status' de PostgreSQL tabla 'jobs'.
 *
 * Transiciones (quién las ejecuta):
 *   PENDIENTE   → Python  (use_cases.py línea 34, INSERT)
 *   PROCESANDO  → Java    (AnalysisService.java línea 66, UPDATE)
 *   COMPLETADO  → Java    (AnalysisService.java línea 83, UPDATE)
 */
public enum JobStatus {
    PENDIENTE,
    PROCESANDO,
    COMPLETADO
}
