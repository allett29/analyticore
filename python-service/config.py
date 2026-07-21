"""
Capa de Infraestructura — Configuración externa (variables de entorno de Render).

Define las URLs de los buses de comunicación:
  DATABASE_URL     → PostgreSQL (usado en infrastructure/database.py línea 32)
  JAVA_SERVICE_URL → Servicio Java REST (usado en infrastructure/java_client.py línea 18)
"""
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env")

    database_url: str = "postgresql://analyticore:analyticore@localhost:5432/analyticore"
    java_service_url: str = "http://localhost:8080"
    demo_step_delay: float = 1.2  # Solo panel visual de demo


settings = Settings()
