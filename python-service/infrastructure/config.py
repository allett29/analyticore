"""
Capa de Infraestructura — Configuración externa (variables de entorno de Render).
"""
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env")

    database_url: str = "postgresql://analyticore:analyticore@localhost:5432/analyticore"
    java_service_url: str = "http://localhost:8080"


settings = Settings()
