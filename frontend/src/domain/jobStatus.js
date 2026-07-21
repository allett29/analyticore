/**
 * Capa de Dominio — Estados del ciclo de vida de un job.
 * Sincronizado con python-service/domain/models.py y PostgreSQL columna 'status'.
 */
export const JobStatus = {
  PENDIENTE: 'PENDIENTE',
  PROCESANDO: 'PROCESANDO',
  COMPLETADO: 'COMPLETADO',
}

export const POLL_INTERVAL_MS = 1000
