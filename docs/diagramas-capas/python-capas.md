# Diagrama de Capas — Python Service (FastAPI)

Arquitectura limpia del Servicio de Submisión.

```mermaid
graph TB
    subgraph Presentación
        ROUTES[presentation/routes.py<br/>Endpoints REST]
        MAIN[presentation/main.py<br/>App FastAPI + CORS]
        DASH[presentation/dashboard.py<br/>Panel demo]
    end

    subgraph Aplicación
        UC_SUB[application/use_cases.py<br/>SubmitTextUseCase]
        UC_GET[application/use_cases.py<br/>GetJobStatusUseCase]
    end

    subgraph Dominio
        MODELS[domain/models.py<br/>Job, JobStatus]
    end

    subgraph Infraestructura
        DB[infrastructure/database.py<br/>SQLAlchemy + PostgreSQL]
        JAVA[infrastructure/java_client.py<br/>Cliente HTTP → Java]
        CFG[infrastructure/config.py<br/>Variables de entorno]
    end

    subgraph Externo
        PG[(PostgreSQL)]
        JV[Servicio Java<br/>POST /api/analyze]
    end

    MAIN --> ROUTES
    MAIN --> DASH
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
    DASH --> DB
```

## Capas y archivos

| Capa | Archivo | Responsabilidad |
|---|---|---|
| **Presentación** | `presentation/routes.py` | `POST /api/jobs`, `GET /api/jobs/{id}` |
| **Presentación** | `presentation/main.py` | Configuración FastAPI, CORS, health |
| **Presentación** | `presentation/dashboard.py` | Panel visual de demo |
| **Aplicación** | `application/use_cases.py` | Lógica de negocio y orquestación |
| **Dominio** | `domain/models.py` | Entidad Job y enum JobStatus |
| **Infraestructura** | `infrastructure/database.py` | Persistencia en PostgreSQL |
| **Infraestructura** | `infrastructure/java_client.py` | Llamada REST al servicio Java |
| **Infraestructura** | `infrastructure/config.py` | `DATABASE_URL`, `JAVA_SERVICE_URL` |
