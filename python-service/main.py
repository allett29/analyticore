"""
Capa de Presentación — Punto de entrada del Servicio de Submisión (Python/FastAPI).

BUS DE COMUNICACIÓN: REST/HTTP (no hay cola ni mensajería; cada servicio expone APIs).

Conexiones de este servicio:
  ENTRADA  → Frontend (React)  : POST/GET /api/jobs        (línea 39, api/routes.py)
  SALIDA   → Java (Spring Boot) : POST /api/analyze/{jobId} (línea 27, infrastructure/java_client.py)
  SALIDA   → PostgreSQL (Render): SQL vía SQLAlchemy       (línea 32, infrastructure/database.py)
"""
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from api.routes import router
from api.dashboard import router as dashboard_router
from infrastructure.database import init_db


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Al arrancar: crea tabla 'jobs' en PostgreSQL si no existe (init_db)."""
    init_db()
    yield


app = FastAPI(
    title="AnalytiCore - Servicio de Submisión",
    description="Recibe textos, persiste jobs y orquesta el análisis con el servicio Java.",
    version="1.0.0",
    lifespan=lifespan,
)

# CORS: permite peticiones REST desde el Frontend (origen distinto en Render)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Monta el bus REST hacia el Frontend bajo el prefijo /api
app.include_router(router, prefix="/api")
# Panel visual de demo (no participa en el flujo de negocio)
app.include_router(dashboard_router)


@app.get("/health")
def health_check():
    """Health check para Render — no comunica con otros servicios."""
    return {"status": "ok", "service": "python-submission"}
