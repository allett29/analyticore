# Diagrama de Capas — Java Service (Spring Boot)

Arquitectura limpia del Servicio de Análisis (Worker).

```mermaid
graph TB
    subgraph Presentación
        CTRL[presentation/AnalysisController<br/>POST /api/analyze/{jobId}]
        DASH[presentation/DashboardController<br/>Panel demo]
    end

    subgraph Aplicación
        SVC[application/AnalysisService<br/>Análisis sentimiento + keywords]
        DSVC[application/DashboardService<br/>Estado panel demo]
    end

    subgraph Dominio
        JOB[domain/Job.java<br/>Entidad pura]
        STATUS[domain/JobStatus.java<br/>PENDIENTE/PROCESANDO/COMPLETADO]
    end

    subgraph Infraestructura
        ENTITY[infrastructure/persistence/JobEntity.java<br/>Mapeo JPA]
        REPO[infrastructure/persistence/JobRepository.java<br/>Adaptador PostgreSQL]
        MAPPER[infrastructure/persistence/JobMapper.java<br/>Dominio ↔ JPA]
        DBCFG[infrastructure/config/DatabaseConfig<br/>Conexión PostgreSQL]
    end

    subgraph Externo
        PG[(PostgreSQL)]
        PY[Servicio Python<br/>Notifica nuevo job]
    end

    PY -->|REST POST| CTRL
    CTRL --> SVC
    DASH --> DSVC
    DSVC --> REPO
    SVC --> JOB
    SVC --> STATUS
    SVC --> REPO
    REPO --> MAPPER
    REPO --> ENTITY
    ENTITY --> PG
    DBCFG --> REPO
```

## Capas y archivos

| Capa | Archivo | Responsabilidad |
|---|---|---|
| **Presentación** | `presentation/AnalysisController.java` | Endpoint REST interno |
| **Presentación** | `presentation/DashboardController.java` | Panel visual de demo |
| **Aplicación** | `application/AnalysisService.java` | Análisis de sentimiento y keywords |
| **Dominio** | `domain/Job.java` | Entidad de dominio (sin JPA) |
| **Dominio** | `domain/JobStatus.java` | Estados del ciclo de vida |
| **Infraestructura** | `infrastructure/persistence/JobEntity.java` | Entidad JPA |
| **Infraestructura** | `infrastructure/persistence/JobRepository.java` | Acceso a PostgreSQL |
| **Infraestructura** | `infrastructure/config/DatabaseConfig.java` | Configuración externa de BD |
