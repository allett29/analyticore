"""
═══ SALIDA REST (Python → Java) — PASO 3 ═══

Envía la notificación al Servicio de Análisis (Java/Spring Boot).
Recibe en: java-service/presentation/AnalysisController.java

Implementa AnalysisClientPort (puerto definido en domain/ports/).
"""
import httpx
from uuid import UUID

from domain.ports.analysis_client_port import AnalysisClientPort
from infrastructure.config import settings


class HttpJavaAnalysisClient(AnalysisClientPort):
    def __init__(self, base_url: str | None = None):
        self.base_url = (base_url or settings.java_service_url).rstrip("/")

    def trigger_analysis(self, job_id: UUID) -> None:
        """
        PASO 3 — Llamada REST síncrona a Java para iniciar el análisis.
        POST {JAVA_SERVICE_URL}/api/analyze/{jobId}
        → AnalysisController.analyze() marca PROCESANDO y lanza el worker.
        """
        url = f"{self.base_url}/api/analyze/{job_id}"
        with httpx.Client(timeout=60.0) as client:
            response = client.post(url)
            response.raise_for_status()
