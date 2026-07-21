# Diagrama de Capas — Java Service (Spring Boot)

Arquitectura limpia del Servicio de Análisis (Worker).

```mermaid
graph TB
    subgraph Presentación
        CTRL[controller/AnalysisController<br/>POST /api/analyze/{jobId}]
    end

    subgraph Aplicación
        SVC[service/AnalysisService<br/>Análisis sentimiento + keywords]
    end

    subgraph Dominio
        JOB[domain/Job.java<br/>Entidad de negocio]
        STATUS[domain/JobStatus.java<br/>PENDIENTE/PROCESANDO/COMPLETADO]
    end

    subgraph Infraestructura
        REPO[repository/JobRepository<br/>Spring Data JPA]
        DBCFG[config/DatabaseConfig<br/>Conexión PostgreSQL externa]
        PROPS[application.properties<br/>Configuración Render]
    end

    subgraph Externo
        PG[(PostgreSQL)]
        PY[Servicio Python<br/>Notifica nuevo job]
    end

    PY -->|REST POST| CTRL
    CTRL --> SVC
    SVC --> JOB
    SVC --> STATUS
    SVC --> REPO
    REPO --> PG
    DBCFG --> REPO
    PROPS --> DBCFG
```

## Capas y archivos

| Capa | Archivo | Responsabilidad |
|---|---|---|
| **Presentación** | `controller/AnalysisController.java` | Endpoint REST interno |
| **Aplicación** | `service/AnalysisService.java` | Análisis de sentimiento y keywords |
| **Dominio** | `domain/Job.java` | Entidad Job |
| **Dominio** | `domain/JobStatus.java` | Estados del ciclo de vida |
| **Infraestructura** | `repository/JobRepository.java` | Acceso a PostgreSQL vía JPA |
| **Infraestructura** | `config/DatabaseConfig.java` | Configuración externa de BD |
| **Infraestructura** | `application.properties` | Variables de entorno Render |
