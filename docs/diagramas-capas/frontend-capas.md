# Diagrama de Capas — Frontend (React + Nginx)

Arquitectura limpia del componente Frontend.

```mermaid
graph TB
    subgraph Presentación
        MAIN[presentation/main.jsx<br/>Punto de entrada]
        APP[presentation/App.jsx<br/>Orquestador y polling]
        TA[presentation/components/TextAnalyzer.jsx<br/>Formulario]
        RP[presentation/components/ResultsPanel.jsx<br/>Resultados]
        FM[presentation/components/FrontendMonitor.jsx<br/>Panel demo]
    end

    subgraph Dominio
        DOM[domain/jobStatus.js<br/>Estados y constantes]
    end

    subgraph Aplicación
        API[application/api.js<br/>Cliente REST HTTP]
    end

    subgraph Infraestructura
        NG[Nginx<br/>Servidor web de producción]
        VITE[Vite Build<br/>Compilación estática]
    end

    subgraph Externo
        PY[Servicio Python<br/>POST/GET /api/jobs]
    end

    MAIN --> APP
    APP --> TA
    APP --> RP
    APP --> FM
    APP --> DOM
    APP --> API
    API -->|REST JSON| PY
    VITE -->|dist/| NG
    NG -->|Sirve SPA| MAIN
```

## Capas y archivos

| Capa | Archivo | Responsabilidad |
|---|---|---|
| **Presentación** | `src/presentation/App.jsx` | Estado global, envío y polling |
| **Presentación** | `src/presentation/components/TextAnalyzer.jsx` | Formulario de texto |
| **Presentación** | `src/presentation/components/ResultsPanel.jsx` | Muestra sentimiento y keywords |
| **Presentación** | `src/presentation/components/FrontendMonitor.jsx` | Panel visual de demo |
| **Dominio** | `src/domain/jobStatus.js` | Estados del job y constantes |
| **Aplicación** | `src/application/api.js` | Comunicación REST con Python |
| **Infraestructura** | `nginx.conf` | Servidor web ligero (producción) |
| **Infraestructura** | `Dockerfile` | Build multi-stage: Vite + Nginx |
