"""
Capa de Aplicación — Orquestación del flujo entre PostgreSQL y Java.

RECIBE desde: presentation/routes.py
SALE hacia:   infrastructure/database.py (PostgreSQL)
              infrastructure/java_client.py (Servicio Java)
"""
from uuid import UUID, uuid4

from domain.models import Job, JobStatus
from domain.ports.analysis_client_port import AnalysisClientPort
from domain.ports.job_repository_port import JobRepositoryPort


class SubmitTextUseCase:
    def __init__(self, repository: JobRepositoryPort, analysis_client: AnalysisClientPort):
        self.repository = repository
        self.analysis_client = analysis_client

    def execute(self, text: str) -> Job:
        job = Job(id=uuid4(), text=text, status=JobStatus.PENDIENTE)

        # PASO 2a — Persiste en PostgreSQL con estado PENDIENTE (database.py save)
        self.repository.save(job)

        # PASO 3 — Notifica al Servicio Java vía REST (java_client.py trigger_analysis)
        # Destino: AnalysisController.java POST /api/analyze/{jobId}
        self.analysis_client.trigger_analysis(job.id)

        # Relee estado actualizado desde PostgreSQL y lo devuelve al Frontend
        return self.repository.find_by_id(job.id) or job


class GetJobStatusUseCase:
    def __init__(self, repository: JobRepositoryPort):
        self.repository = repository

    def execute(self, job_id: UUID) -> Job | None:
        # PASO 5 — Lee de PostgreSQL para responder al polling del Frontend
        return self.repository.find_by_id(job_id)
