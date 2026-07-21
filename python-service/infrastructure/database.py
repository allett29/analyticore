"""
Capa de Infraestructura: acceso a PostgreSQL.
Externaliza todo el estado (patrón stateless del servicio).
"""
from datetime import datetime
from uuid import UUID

from sqlalchemy import Column, DateTime, Float, String, Text, create_engine
from sqlalchemy.dialects.postgresql import UUID as PGUUID
from sqlalchemy.orm import Session, declarative_base, sessionmaker

from config import settings
from domain.models import Job, JobStatus

Base = declarative_base()


class JobORM(Base):
    """Modelo ORM que mapea la tabla 'jobs' en PostgreSQL."""
    __tablename__ = "jobs"

    id = Column(PGUUID(as_uuid=True), primary_key=True)
    text = Column(Text, nullable=False)
    status = Column(String(20), nullable=False)
    sentiment = Column(String(20), nullable=True)
    score = Column(Float, nullable=True)
    keywords = Column(Text, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)


engine = create_engine(settings.database_url)
SessionLocal = sessionmaker(bind=engine)


def init_db():
    """Crea la tabla si no existe (compatible con schema.sql)."""
    Base.metadata.create_all(bind=engine)


class JobRepository:
    """Repositorio: traduce entre entidades de dominio y registros de BD."""

    def save(self, job: Job) -> Job:
        with Session(engine) as session:
            orm_job = JobORM(
                id=job.id,
                text=job.text,
                status=job.status.value,
                sentiment=job.sentiment,
                score=job.score,
                keywords=job.keywords,
            )
            session.add(orm_job)
            session.commit()
            return self._to_domain(orm_job)

    def find_by_id(self, job_id: UUID) -> Job | None:
        with Session(engine) as session:
            orm_job = session.get(JobORM, job_id)
            if orm_job is None:
                return None
            return self._to_domain(orm_job)

    @staticmethod
    def _to_domain(orm_job: JobORM) -> Job:
        return Job(
            id=orm_job.id,
            text=orm_job.text,
            status=JobStatus(orm_job.status),
            sentiment=orm_job.sentiment,
            score=orm_job.score,
            keywords=orm_job.keywords,
            created_at=orm_job.created_at,
            updated_at=orm_job.updated_at,
        )
