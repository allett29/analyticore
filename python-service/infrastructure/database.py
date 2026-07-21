"""
Capa de Infraestructura — Acceso a PostgreSQL (Render).

BUS DE COMUNICACIÓN: SQL vía SQLAlchemy → PostgreSQL tabla 'jobs'
URL de conexión: config.py settings.database_url (variable DATABASE_URL en Render)

Quién usa este módulo:
  application/use_cases.py → save()       INSERT (Python crea job PENDIENTE)
  application/use_cases.py → find_by_id() SELECT (Frontend consulta vía polling)
  (Java también lee/escribe la misma tabla vía JPA en java-service/repository/)
"""
from datetime import datetime
from uuid import UUID

from sqlalchemy import Column, DateTime, Float, String, Text, create_engine
from sqlalchemy.dialects.postgresql import UUID as PGUUID
from sqlalchemy.orm import Session, declarative_base, sessionmaker

from infrastructure.config import settings
from domain.models import Job, JobStatus
from domain.ports.job_repository_port import JobRepositoryPort

Base = declarative_base()


class JobORM(Base):
    """Mapeo ORM → tabla PostgreSQL 'jobs' (schema en database/schema.sql)."""
    __tablename__ = "jobs"

    id = Column(PGUUID(as_uuid=True), primary_key=True)
    text = Column(Text, nullable=False)
    status = Column(String(20), nullable=False)
    sentiment = Column(String(20), nullable=True)
    score = Column(Float, nullable=True)
    keywords = Column(Text, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)


# Conexión al bus PostgreSQL — URL externa desde Render (DATABASE_URL)
engine = create_engine(settings.database_url)
SessionLocal = sessionmaker(bind=engine)


def init_db():
    """Al arrancar (main.py lifespan): crea tabla 'jobs' si no existe."""
    Base.metadata.create_all(bind=engine)


class SqlAlchemyJobRepository(JobRepositoryPort):
    """Adaptador de infraestructura — implementa JobRepositoryPort con SQLAlchemy."""

    def save(self, job: Job) -> Job:
        """BUS → PostgreSQL: INSERT INTO jobs (id, text, status, ...) VALUES (...)"""
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
        """BUS → PostgreSQL: SELECT * FROM jobs WHERE id = {jobId}"""
        with Session(engine) as session:
            orm_job = session.get(JobORM, job_id)
            if orm_job is None:
                return None
            return self._to_domain(orm_job)

    def find_latest(self) -> Job | None:
        """Solo para panel de monitoreo — lee desde PostgreSQL (stateless)."""
        with Session(engine) as session:
            orm_job = (
                session.query(JobORM)
                .order_by(JobORM.created_at.desc())
                .first()
            )
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
