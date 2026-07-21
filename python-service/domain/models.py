"""
Capa de Dominio — Entidades puras de negocio.

No se comunica con ningún servicio externo.
Compartida conceptualmente con java-service/domain/Job.java y JobStatus.java.
El estado se persiste en PostgreSQL tabla 'jobs' (vía capa de infraestructura).
"""
from dataclasses import dataclass
from datetime import datetime
from enum import Enum
from typing import Optional
from uuid import UUID


class JobStatus(str, Enum):
    """Estados del ciclo de vida — sincronizados con la columna 'status' en PostgreSQL."""
    PENDIENTE = "PENDIENTE"      # Creado por Python (use_cases.py línea 34)
    PROCESANDO = "PROCESANDO"    # Actualizado por Java (AnalysisService.java línea 66)
    COMPLETADO = "COMPLETADO"    # Actualizado por Java (AnalysisService.java línea 83)


@dataclass
class Job:
    """Entidad de dominio: un trabajo de análisis de texto."""
    id: UUID
    text: str
    status: JobStatus
    sentiment: Optional[str] = None   # Escrito por Java en PostgreSQL
    score: Optional[float] = None     # Escrito por Java en PostgreSQL
    keywords: Optional[str] = None    # Escrito por Java en PostgreSQL (JSON string)
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None
