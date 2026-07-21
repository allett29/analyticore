"""
Capa de Dominio: entidades y reglas de negocio puras.
No depende de FastAPI, SQLAlchemy ni HTTP.
"""
from dataclasses import dataclass
from datetime import datetime
from enum import Enum
from typing import Optional
from uuid import UUID


class JobStatus(str, Enum):
    """Estados del ciclo de vida de un trabajo de análisis."""
    PENDIENTE = "PENDIENTE"
    PROCESANDO = "PROCESANDO"
    COMPLETADO = "COMPLETADO"


@dataclass
class Job:
    """Entidad de dominio que representa un trabajo de análisis de texto."""
    id: UUID
    text: str
    status: JobStatus
    sentiment: Optional[str] = None
    score: Optional[float] = None
    keywords: Optional[str] = None
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None
