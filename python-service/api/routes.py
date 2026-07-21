"""
Capa de Presentación: endpoints REST expuestos al Frontend.
Punto de entrada HTTP del Servicio de Submisión.
"""
import time
from uuid import UUID

from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field

from application.use_cases import GetJobStatusUseCase, SubmitTextUseCase
from config import settings
from infrastructure.activity_tracker import on_received
from infrastructure.database import JobRepository
from infrastructure.java_client import JavaAnalysisClient

router = APIRouter()

# Inyección manual simple (sin framework DI para mantener el prototipo claro)
job_repository = JobRepository()
java_client = JavaAnalysisClient()
submit_use_case = SubmitTextUseCase(job_repository, java_client)
get_status_use_case = GetJobStatusUseCase(job_repository)


class SubmitTextRequest(BaseModel):
    """DTO de entrada: texto enviado por el Frontend."""
    text: str = Field(..., min_length=1, max_length=5000, description="Texto a analizar")


class JobResponse(BaseModel):
    """DTO de salida: estado y resultados del job para el Frontend."""
    jobId: str
    status: str
    sentiment: str | None = None
    score: float | None = None
    keywords: list[str] | None = None


@router.post("/jobs", response_model=JobResponse, status_code=201)
def submit_job(request: SubmitTextRequest):
    """
    POST /api/jobs
    Flujo: validar → persistir PENDIENTE → llamar Java → devolver jobId al Frontend.
    """
    on_received(request.text)
    time.sleep(settings.demo_step_delay)
    job = submit_use_case.execute(request.text)
    return JobResponse(jobId=str(job.id), status=job.status.value)


@router.get("/jobs/{job_id}", response_model=JobResponse)
def get_job(job_id: UUID):
    """
    GET /api/jobs/{jobId}
    Usado por el Frontend en polling para conocer estado y obtener resultados.
    """
    job = get_status_use_case.execute(job_id)
    if job is None:
        raise HTTPException(status_code=404, detail="Job no encontrado")

    keywords_list = None
    if job.keywords:
        # keywords se almacenan como JSON string en BD (escrito por Java)
        import json
        try:
            keywords_list = json.loads(job.keywords)
        except json.JSONDecodeError:
            keywords_list = [job.keywords]

    return JobResponse(
        jobId=str(job.id),
        status=job.status.value,
        sentiment=job.sentiment,
        score=job.score,
        keywords=keywords_list,
    )
