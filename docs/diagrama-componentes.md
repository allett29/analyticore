# Diagrama de Componentes — AnalytiCore

Arquitectura orientada a servicios desplegada en Render con 3 microservicios en contenedores Docker y PostgreSQL gestionado.

```mermaid
graph TB
    subgraph Usuario
        U[👤 Usuario]
    end

    subgraph Render Cloud
        subgraph Frontend Container
            NG[Nginx]
            RE[React SPA]
            NG --> RE
        end

        subgraph Python Container
            PY[FastAPI<br/>Servicio de Submisión]
        end

        subgraph Java Container
            JV[Spring Boot<br/>Servicio de Análisis]
        end

        subgraph Database
            PG[(PostgreSQL<br/>Render Managed)]
        end
    end

    U -->|1. Introduce texto| RE
    RE -->|2. POST /api/jobs<br/>REST| PY
    PY -->|3. INSERT PENDIENTE| PG
    PY -->|4. POST /api/analyze/{jobId}<br/>REST interna| JV
    JV -->|5. UPDATE PROCESANDO| PG
    JV -->|6. Análisis + UPDATE COMPLETADO| PG
    RE -->|7. GET /api/jobs/{jobId}<br/>Polling REST| PY
    PY -->|8. SELECT estado/resultados| PG
    PY -->|9. Respuesta JSON| RE
    RE -->|10. Muestra resultados| U
```

## Descripción de componentes

| Componente | Tecnología | Responsabilidad |
|---|---|---|
| **Frontend** | React + Nginx | Interfaz de usuario (SPA). Envía textos y muestra resultados. |
| **Python Service** | FastAPI | Recibe solicitudes, valida, persiste jobs, orquesta análisis. |
| **Java Service** | Spring Boot | Worker que analiza sentimiento y extrae keywords. |
| **PostgreSQL** | Render Managed | Almacena estado y resultados (stateless en servicios). |

## Comunicación

- **Frontend ↔ Python**: API REST pública (`POST /api/jobs`, `GET /api/jobs/{id}`)
- **Python ↔ Java**: API REST interna (`POST /api/analyze/{jobId}`)
- **Java ↔ PostgreSQL**: JDBC/JPA directo
- **Python ↔ PostgreSQL**: SQLAlchemy directo
