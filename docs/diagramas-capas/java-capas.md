# Diagrama de Capas — Java Service (Spring Boot)

Arquitectura limpia con **inversión de dependencias**: `AnalysisService` depende de `JobRepositoryPort`, no de JPA.

```mermaid
graph TB
    subgraph Presentación
        CTRL[presentation/AnalysisController]
        DASH[presentation/DashboardController]
    end

    subgraph Aplicación
        SVC[application/AnalysisService]
        WORKER[application/AnalysisWorker]
        DSVC[application/DashboardService]
    end

    subgraph Dominio
        JOB[domain/Job.java]
        STATUS[domain/JobStatus.java]
        PORT[domain/port/JobRepositoryPort.java<br/>interfaz]
    end

    subgraph Infraestructura
        ADAPTER[infrastructure/persistence/JpaJobRepositoryAdapter<br/>implementa puerto]
        ENTITY[infrastructure/persistence/JobEntity.java]
        MAPPER[infrastructure/persistence/JobMapper.java]
        JPA[infrastructure/persistence/JpaJobRepository.java]
        DBCFG[infrastructure/config/DatabaseConfig]
    end

    subgraph Externo
        PG[(PostgreSQL)]
        PY[Servicio Python]
    end

    PY -->|REST POST| CTRL
    CTRL --> SVC
    CTRL --> WORKER
    DASH --> DSVC
    SVC --> JOB
    SVC --> STATUS
    SVC --> PORT
    DSVC --> PORT
    WORKER --> SVC
    ADAPTER -.->|implementa| PORT
    ADAPTER --> JPA
    ADAPTER --> MAPPER
    ADAPTER --> ENTITY
    ENTITY --> PG
    DBCFG --> JPA
```

## Regla de dependencia

| Capa | Depende de | No depende de |
|---|---|---|
| **Presentación** | Aplicación | Infraestructura concreta |
| **Aplicación** | Dominio (modelos + **JobRepositoryPort**) | JPA, JDBC, PostgreSQL |
| **Dominio** | Nada externo | Spring, JPA |
| **Infraestructura** | Dominio (puerto + modelos) | Aplicación |

## Archivos clave

| Capa | Archivo | Responsabilidad |
|---|---|---|
| **Dominio** | `domain/port/JobRepositoryPort.java` | Puerto de persistencia |
| **Aplicación** | `application/AnalysisService.java` | Lógica de análisis vía puerto |
| **Infraestructura** | `infrastructure/persistence/JpaJobRepositoryAdapter.java` | Adaptador JPA |
