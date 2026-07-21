"""
Capa de Aplicación: casos de uso que orquestan el flujo de negocio.
Conecta la API (presentación) con la BD (infraestructura) y el servicio Java.
"""
import time
from uuid import UUID, uuid4

from domain.models import Job, JobStatus
from config import settings
from infrastructure.activity_tracker import on_calling_java, on_finished, on_saving, on_validating
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
        delay = settings.demo_step_delay

        # Panel / : registra cada paso interno de Python (con pausa para la demo)
        on_validating()
        time.sleep(delay)

        job = Job(id=uuid4(), text=text, status=JobStatus.PENDIENTE)
        on_saving(str(job.id))
        self.repository.save(job)
        time.sleep(delay)

        on_calling_java(str(job.id))
        time.sleep(delay)
        self.java_client.trigger_analysis(job.id)
        time.sleep(delay)

        on_finished(str(job.id))
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
