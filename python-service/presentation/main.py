"""
Capa de Presentación — Punto de entrada del Servicio de Submisión (Python/FastAPI).

BUS DE COMUNICACIÓN: REST/HTTP (no hay cola ni mensajería; cada servicio expone APIs).

Conexiones de este servicio:
  ENTRADA  → Frontend (React)  : POST/GET /api/jobs        (presentation/routes.py)
  SALIDA   → Java (Spring Boot) : POST /api/analyze/{jobId} (infrastructure/java_client.py)
  SALIDA   → PostgreSQL (Render): SQL vía SQLAlchemy       (infrastructure/database.py)
"""
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from infrastructure.database import init_db
from presentation.dashboard import router as dashboard_router
from presentation.routes import router


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
# Panel de monitoreo (lee estado desde PostgreSQL — stateless)
app.include_router(dashboard_router)


@app.get("/health")
def health_check():
    """Health check para Render — no comunica con otros servicios."""
    return {"status": "ok", "service": "python-submission"}
