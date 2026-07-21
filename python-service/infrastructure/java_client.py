"""
Adaptador de infraestructura — implementa AnalysisClientPort vía REST/HTTP hacia Java.
"""
import httpx
from uuid import UUID

from domain.ports.analysis_client_port import AnalysisClientPort
from infrastructure.config import settings


class HttpJavaAnalysisClient(AnalysisClientPort):
    def __init__(self, base_url: str | None = None):
        self.base_url = (base_url or settings.java_service_url).rstrip("/")

    def trigger_analysis(self, job_id: UUID) -> None:
        url = f"{self.base_url}/api/analyze/{job_id}"
        with httpx.Client(timeout=60.0) as client:
            response = client.post(url)
            response.raise_for_status()
