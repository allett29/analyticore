/**
 * Panel de monitoreo INTERNO del Frontend (React).
 * Muestra únicamente lo que hace el Frontend — no los otros servicios.
 */
const PYTHON_URL = import.meta.env.VITE_PYTHON_URL || 'http://localhost:8000'
const JAVA_URL = import.meta.env.VITE_JAVA_URL || 'http://localhost:8080'

// Pasos INTERNOS que ejecuta solo el Frontend
const STEPS = [
  { label: 'Esperando que el usuario escriba un texto' },
  { label: 'Usuario envió el texto — preparando solicitud' },
  { label: 'Enviando POST /api/jobs al Servicio Python' },
  { label: 'Esperando respuesta — consultando estado del job' },
  { label: 'Resultado recibido — mostrando al usuario' },
]

function buildDetail(phase, text, jobId, results) {
  if (phase === 0) return ''
  if (phase === 1) return text ? `Texto: "${text.slice(0, 60)}${text.length > 60 ? '...' : ''}"` : ''
  if (phase === 2) return 'Destino: Servicio Python (FastAPI)'
  if (phase === 3 && jobId) return `Job ID: ${jobId} — polling GET /api/jobs/${jobId}`
  if (phase === 4 && results) {
    return `Sentimiento: ${results.sentiment} (${results.score?.toFixed(2)}) | Keywords: ${results.keywords?.join(', ')}`
  }
  return ''
}

export default function FrontendMonitor({
  phase,
  text,
  jobId,
  results,
  isRunning,
}) {
  return (
    <section className="frontend-monitor">
      <div className="monitor-header">
        <span className="monitor-badge">⚛️ SERVICIO FRONTEND</span>
        <h2>Frontend — React + Nginx</h2>
        <p>Interfaz de usuario · envía textos y muestra resultados</p>
      </div>

      {/* Mensaje en vivo del proceso actual */}
      <div className={`monitor-live ${isRunning ? 'monitor-live--active' : ''}`}>
        <span className="monitor-live-label">Proceso actual</span>
        <p className={`monitor-live-msg ${isRunning ? 'monitor-live-msg--pulse' : ''}`}>
          {STEPS[phase]?.label || STEPS[0].label}
        </p>
        {buildDetail(phase, text, jobId, results) && (
          <p className="monitor-live-detail">{buildDetail(phase, text, jobId, results)}</p>
        )}
      </div>

      {/* Lista de pasos internos del Frontend */}
      <div className="monitor-steps">
        {STEPS.map((step, idx) => {
          let state = 'pending'
          if (idx < phase) state = 'done'
          else if (idx === phase && (isRunning || phase === 4)) state = 'active'
          else if (phase < 0 && idx === 0) state = 'active'

          return (
            <div key={idx} className={`monitor-step monitor-step--${state}`}>
              <span className="monitor-step-icon">{state === 'done' ? '✓' : idx + 1}</span>
              <span className="monitor-step-text">{step.label}</span>
            </div>
          )
        })}
      </div>

      <div className="monitor-links">
        <p>Abre en pestañas separadas para ver el proceso interno de cada servicio:</p>
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
