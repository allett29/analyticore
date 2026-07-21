# AnalytiCore

Prototipo de arquitectura orientada a servicios en la nube para análisis de sentimiento y extracción de palabras clave.

## Estructura del repositorio

```
AnalytiCore/
├── frontend/           # React SPA servida con Nginx
├── python-service/     # API de submisión y orquestación (FastAPI)
├── java-service/       # Worker de análisis (Spring Boot)
├── docs/               # Diagramas e informe ejecutivo
├── database/           # Esquema SQL de referencia
└── docker-compose.yml  # Entorno local completo
```

### Arquitectura limpia por componente

**Frontend** — expande `frontend/src/`:

```
src/
├── presentation/          # UI React (App, componentes, estilos)
│   ├── main.jsx
│   ├── App.jsx
│   ├── index.css
│   └── components/
├── application/           # Casos de uso (dependen de puertos)
│   └── jobUseCases.js
├── domain/                # Constantes y puertos (interfaces)
│   ├── jobStatus.js
│   └── ports/jobGatewayPort.js
└── infrastructure/        # Adaptadores concretos (fetch, Nginx en raíz)
    └── http/pythonJobGateway.js
```

**Python** — expande `python-service/`:

```
python-service/
├── presentation/          # FastAPI (endpoints, app, panel)
│   ├── main.py
│   ├── routes.py
│   └── dashboard.py
├── application/           # Casos de uso
│   └── use_cases.py
├── domain/                # Entidades y puertos (interfaces)
│   ├── models.py
│   └── ports/
│       ├── job_repository_port.py
│       └── analysis_client_port.py
└── infrastructure/        # Adaptadores concretos
    ├── database.py        # SqlAlchemyJobRepository
    ├── java_client.py     # HttpJavaAnalysisClient
    ├── dependencies.py    # Composition root
    └── config.py
```

**Java** — expande `java-service/src/main/java/com/analyticore/analyseservice/`:

```
analyseservice/
├── presentation/          # Controllers REST
│   ├── AnalysisController.java
│   └── DashboardController.java
├── application/           # Servicios y worker asíncrono
│   ├── AnalysisService.java
│   ├── AnalysisWorker.java
│   └── DashboardService.java
├── domain/                # Modelo y puertos
│   ├── Job.java
│   ├── JobStatus.java
│   └── port/JobRepositoryPort.java
└── infrastructure/        # Adaptadores JPA y config
    ├── config/
    └── persistence/       # JpaJobRepositoryAdapter, JobEntity...
```

## Flujo de datos

1. El usuario envía texto desde el **Frontend (React)**.
2. El **Servicio Python** valida, guarda el job como `PENDIENTE` en PostgreSQL y llama al servicio Java.
3. El **Servicio Java** cambia el estado a `PROCESANDO`, analiza el texto y guarda resultados como `COMPLETADO`.
4. El **Frontend** consulta periódicamente el estado usando el `jobId`.

## Requisitos locales

- Docker y Docker Compose
- (Opcional) Node.js 20+, Python 3.12+, Java 21+ para desarrollo sin Docker

## Ejecución local con Docker

```bash
docker compose up --build
```

| Servicio        | URL local              |
|-----------------|------------------------|
| Frontend        | http://localhost:3000  |
| Python API      | http://localhost:8000  |
| Java API        | http://localhost:8080  |
| PostgreSQL      | localhost:5432         |

## Variables de entorno

| Variable           | Servicio | Descripción                              |
|--------------------|----------|------------------------------------------|
| `DATABASE_URL`     | Python, Java | Conexión PostgreSQL (Render la provee) |
| `JAVA_SERVICE_URL` | Python   | URL del servicio Java                    |
| `VITE_API_URL`     | Frontend | URL del servicio Python (build time)     |

## Despliegue en Render

Ver `docs/despliegue-render.md` para los pasos detallados.
