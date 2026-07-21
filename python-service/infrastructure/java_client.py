"""
Capa de Infraestructura: cliente HTTP para comunicarse con el Servicio Java.
Comunicación REST interna entre microservicios (punto #3 del flujo de datos).
"""
import httpx

from config import settings
from uuid import UUID


class JavaAnalysisClient:
    """
    Cliente REST que notifica al Servicio Java que un job está listo para analizar.
    URL configurada externamente vía JAVA_SERVICE_URL (Render).
    """

    def __init__(self, base_url: str | None = None):
        self.base_url = (base_url or settings.java_service_url).rstrip("/")

    def trigger_analysis(self, job_id: UUID) -> None:
        """
        POST /api/analyze/{jobId} — llamada síncrona al worker Java.
        Java leerá el texto desde PostgreSQL usando el jobId.
        """
        url = f"{self.base_url}/api/analyze/{job_id}"
        with httpx.Client(timeout=60.0) as client:
            response = client.post(url)
            response.raise_for_status()
