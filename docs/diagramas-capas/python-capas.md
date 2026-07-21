# Diagrama de Capas — Python Service (FastAPI)

Arquitectura limpia del Servicio de Submisión.

```mermaid
graph TB
    subgraph Presentación
        ROUTES[api/routes.py<br/>Endpoints REST]
        MAIN[main.py<br/>App FastAPI + CORS]
    end

    subgraph Aplicación
        UC_SUB[SubmitTextUseCase<br/>Crear job + orquestar]
        UC_GET[GetJobStatusUseCase<br/>Consultar estado]
    end

    subgraph Dominio
        MODELS[domain/models.py<br/>Job, JobStatus]
    end

    subgraph Infraestructura
        DB[infrastructure/database.py<br/>SQLAlchemy + PostgreSQL]
        JAVA[infrastructure/java_client.py<br/>Cliente HTTP → Java]
        CFG[config.py<br/>Variables de entorno]
    end

    subgraph Externo
        PG[(PostgreSQL)]
        JV[Servicio Java<br/>POST /api/analyze]
    end

    MAIN --> ROUTES
    ROUTES --> UC_SUB
    ROUTES --> UC_GET
    UC_SUB --> MODELS
    UC_GET --> MODELS
    UC_SUB --> DB
    UC_SUB --> JAVA
    UC_GET --> DB
    DB --> PG
    JAVA -->|REST| JV
    CFG --> DB
    CFG --> JAVA
```

## Capas y archivos

| Capa | Archivo | Responsabilidad |
|---|---|---|
| **Presentación** | `api/routes.py` | `POST /api/jobs`, `GET /api/jobs/{id}` |
| **Presentación** | `main.py` | Configuración FastAPI, CORS, health |
| **Aplicación** | `application/use_cases.py` | Lógica de negocio y orquestación |
| **Dominio** | `domain/models.py` | Entidad Job y enum JobStatus |
| **Infraestructura** | `infrastructure/database.py` | Persistencia en PostgreSQL |
| **Infraestructura** | `infrastructure/java_client.py` | Llamada REST al servicio Java |
| **Infraestructura** | `config.py` | `DATABASE_URL`, `JAVA_SERVICE_URL` |
