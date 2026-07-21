/**
 * Componente raíz de la SPA.
 * Orquesta la UI: formulario de texto, envío al Python y polling de resultados.
 */
import { useState, useRef } from 'react'
import TextAnalyzer from './components/TextAnalyzer'
import ResultsPanel from './components/ResultsPanel'
import FrontendMonitor from './components/FrontendMonitor'
import { submitText, getJobStatus } from './services/api'

const POLL_INTERVAL_MS = 2000

/** Mapea el estado del job al paso visual global del flujo */
function statusToFlowStep(status) {
  if (status === 'PENDIENTE') return 3
  if (status === 'PROCESANDO') return 5
  if (status === 'COMPLETADO') return 7
  return 0
}

export default function App() {
  const [text, setText] = useState('')
  const [jobId, setJobId] = useState(null)
  const [status, setStatus] = useState(null)
  const [results, setResults] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [flowStep, setFlowStep] = useState(-1)
  const stepTimerRef = useRef(null)

  const startFlowAnimation = () => {
    setFlowStep(0)
    let step = 0
    stepTimerRef.current = setInterval(() => {
      step = Math.min(step + 1, 4)
      setFlowStep(step)
    }, 700)
  }

  const stopFlowAnimation = () => {
    if (stepTimerRef.current) {
      clearInterval(stepTimerRef.current)
      stepTimerRef.current = null
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setResults(null)
    setStatus(null)
    setJobId(null)
    startFlowAnimation()

    try {
      const response = await submitText(text)
      stopFlowAnimation()
      setJobId(response.jobId)

      const job = await getJobStatus(response.jobId)
      setStatus(job.status)
      setFlowStep(statusToFlowStep(job.status))

      if (job.status === 'COMPLETADO') {
        setResults({ sentiment: job.sentiment, score: job.score, keywords: job.keywords })
        setLoading(false)
        return
      }

      pollJobStatus(response.jobId)
    } catch (err) {
      stopFlowAnimation()
      setFlowStep(-1)
      setError(err.message || 'Error al enviar el texto')
      setLoading(false)
    }
  }

  const pollJobStatus = (id) => {
    const intervalId = setInterval(async () => {
      try {
        const job = await getJobStatus(id)
        setStatus(job.status)
        setFlowStep(statusToFlowStep(job.status))

        if (job.status === 'COMPLETADO') {
          setResults({ sentiment: job.sentiment, score: job.score, keywords: job.keywords })
          setLoading(false)
          clearInterval(intervalId)
        }
      } catch (err) {
        stopFlowAnimation()
        setFlowStep(-1)
        setError(err.message || 'Error al consultar el estado')
        setLoading(false)
        clearInterval(intervalId)
      }
    }, POLL_INTERVAL_MS)
  }

  return (
    <div className="app">
      <header className="header">
        <h1>AnalytiCore</h1>
        <p>Análisis de sentimiento y palabras clave</p>
      </header>

      <main className="main">
        <FrontendMonitor flowStep={flowStep} status={status} isRunning={loading} />

        <TextAnalyzer
          text={text}
          onTextChange={setText}
          onSubmit={handleSubmit}
          loading={loading}
        />

        {error && <div className="error">{error}</div>}

        {jobId && (
          <div className="status-bar">
            <span>Job ID: <code>{jobId}</code></span>
            {status && <span className={`badge badge-${status.toLowerCase()}`}>{status}</span>}
          </div>
        )}

        <ResultsPanel results={results} loading={loading && status !== 'COMPLETADO'} />
      </main>

      <footer className="footer">
        <p>Prototipo SOA — React + Python + Java + PostgreSQL</p>
      </footer>
    </div>
  )
}
