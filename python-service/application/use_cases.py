"""
Capa de Aplicación — Casos de uso que orquestan el flujo de negocio.

Flujo POST /api/jobs:
  1. Persistir job en PostgreSQL con estado PENDIENTE
  2. Llamada REST síncrona a Java para iniciar el análisis (Java pasa a PROCESANDO)
  3. Devolver jobId al frontend (estado leído desde PostgreSQL)
"""
from uuid import UUID, uuid4

from domain.models import Job, JobStatus
from infrastructure.database import JobRepository
from infrastructure.java_client import JavaAnalysisClient


class SubmitTextUseCase:
    def __init__(self, repository: JobRepository, java_client: JavaAnalysisClient):
        self.repository = repository
        self.java_client = java_client

    def execute(self, text: str) -> Job:
        job = Job(id=uuid4(), text=text, status=JobStatus.PENDIENTE)
        self.repository.save(job)

        # REST síncrono: notifica a Java que el trabajo está listo
        self.java_client.trigger_analysis(job.id)

        return self.repository.find_by_id(job.id) or job


class GetJobStatusUseCase:
    def __init__(self, repository: JobRepository):
        self.repository = repository

    def execute(self, job_id: UUID) -> Job | None:
        return self.repository.find_by_id(job_id)
