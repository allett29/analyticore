"""
Punto de entrada del Servicio de Submisión (Python).
Expone la API REST que recibe solicitudes del Frontend y orquesta el análisis con Java.
"""
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from api.routes import router
from infrastructure.database import init_db


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Inicializa la base de datos al arrancar el servicio."""
    init_db()
    yield


app = FastAPI(
    title="AnalytiCore - Servicio de Submisión",
    description="Recibe textos, persiste jobs y orquesta el análisis con el servicio Java.",
    version="1.0.0",
    lifespan=lifespan,
)

# CORS: permite que el Frontend (React/Nginx) llame a esta API desde otro origen
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Registra las rutas REST bajo /api
app.include_router(router, prefix="/api")


@app.get("/health")
def health_check():
    """Endpoint de salud para Render y monitoreo."""
    return {"status": "ok", "service": "python-submission"}
