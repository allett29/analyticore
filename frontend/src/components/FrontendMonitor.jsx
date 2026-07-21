/**
 * Panel de monitoreo del Frontend (React).
 * Muestra solo los pasos de ESTE servicio y enlaces a los paneles de Python y Java.
 */
const PYTHON_URL = import.meta.env.VITE_PYTHON_URL || 'http://localhost:8000'
const JAVA_URL = import.meta.env.VITE_JAVA_URL || 'http://localhost:8080'

const FRONTEND_STEPS = [
  { id: 0, action: 'Usuario escribe el texto' },
  { id: 1, action: 'POST /api/jobs → Python' },
  { id: 7, action: 'GET /api/jobs → muestra resultados' },
]

function getFrontendStep(flowStep, status) {
  if (flowStep < 0) return -1
  if (flowStep <= 1) return 0
  if (status === 'COMPLETADO') return 2
  return 1
}

export default function FrontendMonitor({ flowStep, status, isRunning }) {
  const activeIdx = getFrontendStep(flowStep, status)

  return (
    <section className="frontend-monitor">
      <div className="monitor-header">
        <span className="monitor-badge">⚛️ ESTE SERVICIO</span>
        <h2>Frontend — React + Nginx</h2>
        <p>Interfaz de usuario · envía textos y muestra resultados</p>
      </div>

      {/* Pasos propios del Frontend */}
      <div className="monitor-steps">
        {FRONTEND_STEPS.map((step, idx) => {
          let state = 'idle'
          if (activeIdx < 0) state = 'idle'
          else if (idx < activeIdx) state = 'done'
          else if (idx === activeIdx && isRunning) state = 'active'
          else if (idx <= activeIdx) state = 'done'

          return (
            <div key={step.id} className={`monitor-step monitor-step--${state}`}>
              <span className="monitor-step-icon">{state === 'done' ? '✓' : idx + 1}</span>
              <span className="monitor-step-text">{step.action}</span>
            </div>
          )
        })}
      </div>

      {/* Enlaces a los paneles de los otros servicios */}
      <div className="monitor-links">
        <p>Abre en pestañas separadas para ver cada servicio en tiempo real:</p>
        <div className="monitor-link-row">
          <a href={PYTHON_URL} target="_blank" rel="noreferrer" className="monitor-link monitor-link--python">
            🐍 Panel Python
          </a>
          <a href={JAVA_URL} target="_blank" rel="noreferrer" className="monitor-link monitor-link--java">
            ☕ Panel Java
          </a>
        </div>
      </div>
    </section>
  )
}
