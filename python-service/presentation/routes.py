"""
Capa de Presentación — Endpoints REST del Servicio Python.
"""
import json
from uuid import UUID

from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field

from application.use_cases import GetJobStatusUseCase, SubmitTextUseCase
from infrastructure.database import JobRepository
from infrastructure.java_client import JavaAnalysisClient

router = APIRouter()

job_repository = JobRepository()
java_client = JavaAnalysisClient()
submit_use_case = SubmitTextUseCase(job_repository, java_client)
get_status_use_case = GetJobStatusUseCase(job_repository)


class SubmitTextRequest(BaseModel):
    text: str = Field(..., min_length=1, max_length=5000, description="Texto a analizar")


class JobResponse(BaseModel):
    jobId: str
    status: str
    sentiment: str | None = None
    score: float | None = None
    keywords: list[str] | None = None


def _to_response(job) -> JobResponse:
    keywords_list = None
    if job.keywords:
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


@router.post("/jobs", response_model=JobResponse, status_code=201)
def submit_job(request: SubmitTextRequest):
    job = submit_use_case.execute(request.text)
    return _to_response(job)


@router.get("/jobs/{job_id}", response_model=JobResponse)
def get_job(job_id: UUID):
    job = get_status_use_case.execute(job_id)
    if job is None:
        raise HTTPException(status_code=404, detail="Job no encontrado")
    return _to_response(job)
