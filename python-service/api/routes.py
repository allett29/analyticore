"""
Capa de Presentación — Endpoints REST del Servicio Python.

BUS ENTRANTE (quién llama a este archivo):
  Frontend (React) → services/api.js
    - submitText()  línea 16  → POST   /api/jobs
    - getJobStatus() línea 35 → GET    /api/jobs/{jobId}

BUS SALIENTE (a quién llama este archivo vía casos de uso):
  application/use_cases.py → infrastructure/database.py  (PostgreSQL)
  application/use_cases.py → infrastructure/java_client.py (Servicio Java)
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

job_repository = JobRepository()
java_client = JavaAnalysisClient()
submit_use_case = SubmitTextUseCase(job_repository, java_client)
get_status_use_case = GetJobStatusUseCase(job_repository)


class SubmitTextRequest(BaseModel):
    """DTO REST: cuerpo JSON que envía el Frontend en POST /api/jobs."""
    text: str = Field(..., min_length=1, max_length=5000, description="Texto a analizar")


class JobResponse(BaseModel):
    """DTO REST: respuesta JSON que recibe el Frontend."""
    jobId: str
    status: str
    sentiment: str | None = None
    score: float | None = None
    keywords: list[str] | None = None


@router.post("/jobs", response_model=JobResponse, status_code=201)
def submit_job(request: SubmitTextRequest):
    """
    BUS REST ← Frontend (api.js línea 16): POST /api/jobs  body: { "text": "..." }

    Flujo interno de este endpoint:
      línea 48 → application/use_cases.py SubmitTextUseCase.execute()
                 → infrastructure/database.py save()        [PostgreSQL INSERT PENDIENTE]
                 → infrastructure/java_client.py línea 27    [REST POST → Java /api/analyze/{jobId}]
      línea 49 → respuesta JSON { jobId, status } al Frontend
    """
    on_received(request.text)
    time.sleep(settings.demo_step_delay)
    job = submit_use_case.execute(request.text)
    return JobResponse(jobId=str(job.id), status=job.status.value)


@router.get("/jobs/{job_id}", response_model=JobResponse)
def get_job(job_id: UUID):
    """
    BUS REST ← Frontend (api.js línea 35): GET /api/jobs/{jobId}  (polling)

    Flujo interno:
      línea 58 → application/use_cases.py GetJobStatusUseCase.execute()
                 → infrastructure/database.py find_by_id()  [PostgreSQL SELECT]
      línea 71 → respuesta JSON { jobId, status, sentiment, score, keywords } al Frontend
    """
    job = get_status_use_case.execute(job_id)
    if job is None:
        raise HTTPException(status_code=404, detail="Job no encontrado")

    keywords_list = None
    if job.keywords:
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
