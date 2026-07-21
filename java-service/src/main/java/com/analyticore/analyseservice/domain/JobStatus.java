package com.analyticore.analyseservice.domain;

/**
 * Capa de Dominio: estados del ciclo de vida de un trabajo.
 * Compartido conceptualmente con python-service/domain/models.py
 */
public enum JobStatus {
    PENDIENTE,
    PROCESANDO,
    COMPLETADO
}
