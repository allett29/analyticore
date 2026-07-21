"""
Capa de Aplicación — Casos de uso (dependen solo de puertos del dominio).
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
        self.repository.save(job)
        self.analysis_client.trigger_analysis(job.id)
        return self.repository.find_by_id(job.id) or job


class GetJobStatusUseCase:
    def __init__(self, repository: JobRepositoryPort):
        self.repository = repository

    def execute(self, job_id: UUID) -> Job | None:
        return self.repository.find_by_id(job_id)
