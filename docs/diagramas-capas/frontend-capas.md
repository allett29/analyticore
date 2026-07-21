# Diagrama de Capas — Frontend (React + Nginx)

Arquitectura limpia con **inversión de dependencias**: los casos de uso dependen de `JobGatewayPort`, no de `fetch`.

```mermaid
graph TB
    subgraph Presentación
        MAIN[presentation/main.jsx]
        APP[presentation/App.jsx<br/>composition root]
        TA[presentation/components/TextAnalyzer.jsx]
        RP[presentation/components/ResultsPanel.jsx]
        FM[presentation/components/FrontendMonitor.jsx]
    end

    subgraph Aplicación
        UC[application/jobUseCases.js<br/>submitText / getJobStatus]
    end

    subgraph Dominio
        DOM[domain/jobStatus.js]
        PORT[domain/ports/jobGatewayPort.js<br/>interfaz]
    end

    subgraph Infraestructura
        GW[infrastructure/http/pythonJobGateway.js<br/>implementa puerto]
        NG[Nginx]
        VITE[Vite Build]
    end

    subgraph Externo
        PY[Servicio Python]
    end

    MAIN --> APP
    APP --> TA
    APP --> RP
    APP --> FM
    APP --> DOM
    APP --> UC
    APP --> GW
    UC --> PORT
    GW -.->|implementa| PORT
    GW -->|REST JSON| PY
    VITE -->|dist/| NG
    NG --> MAIN
```

## Regla de dependencia

| Capa | Depende de | No depende de |
|---|---|---|
| **Presentación** | Aplicación, Dominio, Infraestructura (solo wiring) | — |
| **Aplicación** | Dominio (**JobGatewayPort**) | fetch, URLs HTTP |
| **Dominio** | Nada externo | React, fetch |
| **Infraestructura** | Dominio (puerto) | Aplicación |

## Archivos clave

| Capa | Archivo | Responsabilidad |
|---|---|---|
| **Dominio** | `domain/ports/jobGatewayPort.js` | Contrato del gateway |
| **Aplicación** | `application/jobUseCases.js` | Casos de uso vía puerto |
| **Infraestructura** | `infrastructure/http/pythonJobGateway.js` | Adaptador fetch → Python |
| **Infraestructura** | `nginx.conf`, `Dockerfile` | Despliegue producción |
