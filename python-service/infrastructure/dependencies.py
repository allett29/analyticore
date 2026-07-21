"""
Composition root — ensambla adaptadores concretos con los puertos (capa presentación/infra).
"""
from functools import lru_cache

from domain.ports.analysis_client_port import AnalysisClientPort
from domain.ports.job_repository_port import JobRepositoryPort
from infrastructure.database import SqlAlchemyJobRepository
from infrastructure.java_client import HttpJavaAnalysisClient


@lru_cache
def get_job_repository() -> JobRepositoryPort:
    return SqlAlchemyJobRepository()


@lru_cache
def get_analysis_client() -> AnalysisClientPort:
    return HttpJavaAnalysisClient()
