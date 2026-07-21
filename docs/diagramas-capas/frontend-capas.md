# Diagrama de Capas — Frontend (React + Nginx)

Arquitectura limpia del componente Frontend.

```mermaid
graph TB
    subgraph Presentación
        APP[App.jsx<br/>Orquestador de estado y polling]
        TA[TextAnalyzer.jsx<br/>Formulario de entrada]
        RP[ResultsPanel.jsx<br/>Visualización de resultados]
    end

    subgraph Aplicación
        API[services/api.js<br/>Cliente REST HTTP]
    end

    subgraph Infraestructura
        NG[Nginx<br/>Servidor web de producción]
        VITE[Vite Build<br/>Compilación estática]
    end

    subgraph Externo
        PY[Servicio Python<br/>POST/GET /api/jobs]
    end

    APP --> TA
    APP --> RP
    APP --> API
    API -->|REST JSON| PY
    VITE -->|dist/| NG
    NG -->|Sirve SPA| APP
```

## Capas y archivos

| Capa | Archivo | Responsabilidad |
|---|---|---|
| **Presentación** | `src/App.jsx` | Estado global, envío y polling |
| **Presentación** | `src/components/TextAnalyzer.jsx` | Formulario de texto |
| **Presentación** | `src/components/ResultsPanel.jsx` | Muestra sentimiento y keywords |
| **Aplicación** | `src/services/api.js` | Comunicación REST con Python |
| **Infraestructura** | `nginx.conf` | Servidor web ligero (producción) |
| **Infraestructura** | `Dockerfile` | Build multi-stage: Vite + Nginx |
