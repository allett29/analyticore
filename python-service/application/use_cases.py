"""
Capa de Aplicación — Casos de uso que orquestan el flujo de negocio.

Comunicaciones que coordina este archivo:
  ENTRADA  ← api/routes.py submit_job() / get_job()
  SALIDA   → infrastructure/database.py     (bus PostgreSQL/SQLAlchemy)
  SALIDA   → infrastructure/java_client.py  (bus REST/HTTP hacia Java)
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
    Caso de uso: recibir texto, persistir PENDIENTE y orquestar análisis en Java.

    Secuencia de comunicación:
      1. Crea entidad Job (dominio, sin I/O)
      2. repository.save()           → PostgreSQL INSERT (estado PENDIENTE)
      3. java_client.trigger_analysis() → REST POST Java /api/analyze/{jobId}
    """

    def __init__(self, repository: JobRepository, java_client: JavaAnalysisClient):
        self.repository = repository
        self.java_client = java_client

    def execute(self, text: str) -> Job:
        delay = settings.demo_step_delay
        on_validating()
        time.sleep(delay)

        job = Job(id=uuid4(), text=text, status=JobStatus.PENDIENTE)

        # BUS → PostgreSQL: INSERT en tabla 'jobs' con status='PENDIENTE'
        on_saving(str(job.id))
        self.repository.save(job)  # infrastructure/database.py línea 44
        time.sleep(delay)

        # BUS → Java (REST): POST {JAVA_SERVICE_URL}/api/analyze/{jobId}
        on_calling_java(str(job.id))
        time.sleep(delay)
        self.java_client.trigger_analysis(job.id)  # infrastructure/java_client.py línea 27
        time.sleep(delay)

        on_finished(str(job.id))
        return job


class GetJobStatusUseCase:
    """
    Caso de uso: consultar estado y resultados de un job.

    BUS → PostgreSQL: SELECT en tabla 'jobs' por id
    Respuesta consumida por Frontend vía api/routes.py get_job() línea 71
    """

    def __init__(self, repository: JobRepository):
        self.repository = repository

    def execute(self, job_id: UUID) -> Job | None:
        return self.repository.find_by_id(job_id)  # infrastructure/database.py línea 58
