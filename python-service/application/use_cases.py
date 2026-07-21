"""
Capa de Aplicación: casos de uso que orquestan el flujo de negocio.
Conecta la API (presentación) con la BD (infraestructura) y el servicio Java.
"""
from uuid import UUID, uuid4

from domain.models import Job, JobStatus
from infrastructure.database import JobRepository
from infrastructure.java_client import JavaAnalysisClient


class SubmitTextUseCase:
    """
    Caso de uso: recibir texto del Frontend, persistir como PENDIENTE
    y notificar al Servicio Java vía REST para iniciar el análisis.
    """

    def __init__(self, repository: JobRepository, java_client: JavaAnalysisClient):
        self.repository = repository
        self.java_client = java_client

    def execute(self, text: str) -> Job:
        # 1. Crear job en PostgreSQL con estado PENDIENTE
        job = Job(
            id=uuid4(),
            text=text,
            status=JobStatus.PENDIENTE,
        )
        self.repository.save(job)

        # 2. Llamada síncrona REST al Servicio Java (punto #3 del flujo de datos)
        self.java_client.trigger_analysis(job.id)

        return job


class GetJobStatusUseCase:
    """
    Caso de uso: consultar estado y resultados de un job.
    Usado por el Frontend en polling periódico con el jobId.
    """

    def __init__(self, repository: JobRepository):
        self.repository = repository

    def execute(self, job_id: UUID) -> Job | None:
        return self.repository.find_by_id(job_id)
