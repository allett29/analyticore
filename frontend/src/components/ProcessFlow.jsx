/**
 * Diagrama animado del flujo SOA entre microservicios.
 * Muestra en qué paso va el proceso al explicar la arquitectura.
 *
 * Estados de cada paso:
 * - pending: aún no ejecutado
 * - active:  en ejecución ahora (pulso animado)
 * - done:    ya completado (check verde)
 */
const STEPS = [
  {
    id: 0,
    service: 'Usuario',
    tech: 'Interfaz',
    action: 'Escribe y envía el texto',
    icon: '👤',
    color: '#94a3b8',
  },
  {
    id: 1,
    service: 'Frontend',
    tech: 'React + Nginx',
    action: 'POST /api/jobs → Python',
    icon: '⚛️',
    color: '#38bdf8',
  },
  {
    id: 2,
    service: 'Python',
    tech: 'FastAPI',
    action: 'Valida el texto recibido',
    icon: '🐍',
    color: '#4ade80',
  },
  {
    id: 3,
    service: 'PostgreSQL',
    tech: 'Base de datos',
    action: 'INSERT estado PENDIENTE',
    icon: '🗄️',
    color: '#fbbf24',
  },
  {
    id: 4,
    service: 'Python → Java',
    tech: 'REST interna',
    action: 'POST /api/analyze/{jobId}',
    icon: '🔗',
    color: '#a78bfa',
  },
  {
    id: 5,
    service: 'Java',
    tech: 'Spring Boot',
    action: 'Analiza sentimiento y keywords',
    icon: '☕',
    color: '#f97316',
  },
  {
    id: 6,
    service: 'PostgreSQL',
    tech: 'Base de datos',
    action: 'UPDATE estado COMPLETADO',
    icon: '🗄️',
    color: '#fbbf24',
  },
  {
    id: 7,
    service: 'Frontend',
    tech: 'React + Nginx',
    action: 'GET /api/jobs → muestra resultados',
    icon: '✅',
    color: '#38bdf8',
  },
]

function getStepState(stepId, activeStep) {
  if (activeStep < 0) return 'idle'
  if (stepId < activeStep) return 'done'
  if (stepId === activeStep) return 'active'
  return 'pending'
}

export default function ProcessFlow({ activeStep, status, isRunning }) {
  const showFlow = activeStep >= 0 || isRunning

  return (
    <section className="process-flow">
      <div className="process-flow-header">
        <h2>Flujo de la arquitectura</h2>
        <p>
          {showFlow
            ? 'Seguimiento en tiempo real del proceso entre servicios'
            : 'Así viajan los datos entre los 3 microservicios'}
        </p>
      </div>

      <div className="process-steps">
        {STEPS.map((step, index) => {
          const state = getStepState(step.id, activeStep)
          const isLast = index === STEPS.length - 1

          return (
            <div key={step.id} className="process-step-wrapper">
              {/* Tarjeta del servicio */}
              <div
                className={`process-step process-step--${state}`}
                style={{ '--step-color': step.color }}
              >
                <div className="process-step-icon">
                  {state === 'done' ? '✓' : step.icon}
                </div>
                <div className="process-step-info">
                  <span className="process-step-service">{step.service}</span>
                  <span className="process-step-tech">{step.tech}</span>
                  <span className="process-step-action">{step.action}</span>
                </div>
                {state === 'active' && (
                  <div className="process-step-pulse" style={{ borderColor: step.color }} />
                )}
              </div>

              {/* Conector animado entre pasos */}
              {!isLast && (
                <div className={`process-connector process-connector--${state}`}>
                  <div className="process-connector-line" />
                  {(state === 'active' || state === 'done') && (
                    <div
                      className="process-connector-dot"
                      style={{ '--step-color': step.color }}
                    />
                  )}
                </div>
              )}
            </div>
          )
        })}
      </div>

      {/* Leyenda de estado actual del job en BD */}
      {status && (
        <div className="process-status-legend">
          <span>Estado en PostgreSQL:</span>
          <span className={`badge badge-${status.toLowerCase()}`}>{status}</span>
        </div>
      )}
    </section>
  )
}
