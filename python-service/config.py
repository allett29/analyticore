"""Configuración externa: lee variables de entorno (Render provee DATABASE_URL)."""
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env")

    database_url: str = "postgresql://analyticore:analyticore@localhost:5432/analyticore"
    java_service_url: str = "http://localhost:8080"
    # Pausa entre pasos del panel / (solo demo visual, en segundos)
    demo_step_delay: float = 1.2


settings = Settings()
