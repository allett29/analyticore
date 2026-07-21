# Diagrama de Capas — Python Service (FastAPI)

Arquitectura limpia con **inversión de dependencias**: la aplicación depende de puertos (interfaces) definidos en el dominio; la infraestructura los implementa.

```mermaid
graph TB
    subgraph Presentación
        ROUTES[presentation/routes.py]
        MAIN[presentation/main.py]
        DASH[presentation/dashboard.py]
        DEPS[infrastructure/dependencies.py<br/>Composition root]
    end

    subgraph Aplicación
        UC_SUB[SubmitTextUseCase]
        UC_GET[GetJobStatusUseCase]
    end

    subgraph Dominio
        MODELS[domain/models.py<br/>Job, JobStatus]
        PORT_REPO[domain/ports/job_repository_port.py<br/>JobRepositoryPort]
        PORT_JAVA[domain/ports/analysis_client_port.py<br/>AnalysisClientPort]
    end

    subgraph Infraestructura
        DB[infrastructure/database.py<br/>SqlAlchemyJobRepository]
        JAVA[infrastructure/java_client.py<br/>HttpJavaAnalysisClient]
        CFG[infrastructure/config.py]
    end

    subgraph Externo
        PG[(PostgreSQL)]
        JV[Servicio Java]
    end

    MAIN --> ROUTES
    MAIN --> DASH
    DEPS --> DB
    DEPS --> JAVA
    ROUTES --> DEPS
    ROUTES --> UC_SUB
    ROUTES --> UC_GET
    DASH --> DEPS
    UC_SUB --> MODELS
    UC_GET --> MODELS
    UC_SUB --> PORT_REPO
    UC_SUB --> PORT_JAVA
    UC_GET --> PORT_REPO
    DB -.->|implementa| PORT_REPO
    JAVA -.->|implementa| PORT_JAVA
    DB --> PG
    JAVA -->|REST| JV
    CFG --> DB
    CFG --> JAVA
```

## Regla de dependencia

| Capa | Depende de | No depende de |
|---|---|---|
| **Presentación** | Aplicación, Dominio, Infraestructura (solo wiring) | — |
| **Aplicación** | Dominio (modelos + **puertos**) | Infraestructura concreta |
| **Dominio** | Nada externo | Aplicación, Infraestructura |
| **Infraestructura** | Dominio (puertos + modelos) | Aplicación |

## Archivos clave

| Capa | Archivo | Responsabilidad |
|---|---|---|
| **Dominio** | `domain/ports/job_repository_port.py` | Puerto de persistencia |
| **Dominio** | `domain/ports/analysis_client_port.py` | Puerto hacia Java |
| **Aplicación** | `application/use_cases.py` | Orquestación vía puertos |
| **Infraestructura** | `infrastructure/database.py` | Adaptador SQLAlchemy |
| **Infraestructura** | `infrastructure/java_client.py` | Adaptador HTTP |
| **Infraestructura** | `infrastructure/dependencies.py` | Inyección de adaptadores |
