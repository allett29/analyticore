"""Puerto de dominio — contrato de persistencia de jobs (inversión de dependencias)."""
from abc import ABC, abstractmethod
from uuid import UUID

from domain.models import Job


class JobRepositoryPort(ABC):
    @abstractmethod
    def save(self, job: Job) -> Job:
        pass

    @abstractmethod
    def find_by_id(self, job_id: UUID) -> Job | None:
        pass

    @abstractmethod
    def find_latest(self) -> Job | None:
        pass
