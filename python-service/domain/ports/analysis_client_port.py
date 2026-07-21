"""Puerto de dominio — contrato para notificar al servicio de análisis Java."""
from abc import ABC, abstractmethod
from uuid import UUID


class AnalysisClientPort(ABC):
    @abstractmethod
    def trigger_analysis(self, job_id: UUID) -> None:
        pass
