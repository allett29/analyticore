# AnalytiCore

Prototipo de arquitectura orientada a servicios en la nube para análisis de sentimiento y extracción de palabras clave.

## Estructura del repositorio

```
AnalytiCore/
├── frontend/           # React SPA servida con Nginx
├── python-service/   # API de submisión y orquestación (FastAPI)
├── java-service/     # Worker de análisis (Spring Boot)
├── docs/             # Diagramas e informe ejecutivo
├── database/         # Esquema SQL de referencia
└── docker-compose.yml  # Entorno local completo
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
