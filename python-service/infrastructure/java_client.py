"""
Capa de Infraestructura — Cliente REST hacia el Servicio Java.

BUS DE COMUNICACIÓN: REST/HTTP (comunicación interna entre microservicios)
  Origen : Python (este archivo)
  Destino: Java → AnalysisController.java línea 28 (@PostMapping /api/analyze/{jobId})
  URL base: config.py settings.java_service_url (variable JAVA_SERVICE_URL en Render)

Invocado desde: application/use_cases.py línea 39 (SubmitTextUseCase.execute)
"""
import httpx

from config import settings
from uuid import UUID


class JavaAnalysisClient:
    """Adaptador REST: Python → Java."""

    def __init__(self, base_url: str | None = None):
        self.base_url = (base_url or settings.java_service_url).rstrip("/")

    def trigger_analysis(self, job_id: UUID) -> None:
        """
        BUS REST → Java:
          Método : POST
          Ruta   : {JAVA_SERVICE_URL}/api/analyze/{jobId}
          Body   : vacío (Java lee el texto desde PostgreSQL usando el jobId)
          Librería: httpx (línea 27-28)
        """
        url = f"{self.base_url}/api/analyze/{job_id}"
        with httpx.Client(timeout=60.0) as client:
            response = client.post(url)
            response.raise_for_status()
