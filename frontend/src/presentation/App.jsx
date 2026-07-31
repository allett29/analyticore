/**
 * Capa de Presentación — Orquestador del Frontend.
 *
 * FLUJO DE COMUNICACIÓN:
 *   handleSubmit()  → jobUseCases → pythonJobGateway → Python (PASO 1)
 *   pollUntilComplete() → pythonJobGateway → Python (PASO 5, polling)
 *
 * Este componente NO habla con Java ni PostgreSQL.
 */
import { useMemo, useState } from 'react'
import { getJobStatusUseCase, submitTextUseCase } from '../application/jobUseCases'
import { JobStatus, POLL_INTERVAL_MS } from '../domain/jobStatus'
import { pythonJobGateway } from '../infrastructure/http/pythonJobGateway'
import FrontendMonitor from './components/FrontendMonitor'
import ResultsPanel from './components/ResultsPanel'
import TextAnalyzer from './components/TextAnalyzer'

function statusToPhase(status, loading) {
  if (!loading && !status) return 0
  if (loading && !status) return 1
  if (status === JobStatus.PENDIENTE) return 2
  if (status === JobStatus.PROCESANDO) return 3
  if (status === JobStatus.COMPLETADO) return 4
  return loading ? 2 : 0
}


export default function App() {
  const [text, setText] = useState('')
  const [jobId, setJobId] = useState(null)
  const [status, setStatus] = useState(null)
  const [results, setResults] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const phase = useMemo(() => statusToPhase(status, loading), [status, loading])

  /** PASO 5 — Polling: repite consulta a Python hasta COMPLETADO */
  const pollUntilComplete = async (id) => {
    const check = async () => {
      // → pythonJobGateway.getJobStatus() → routes.py get_job()
      const job = await getJobStatusUseCase(pythonJobGateway, id)
      setStatus(job.status)

      if (job.status === JobStatus.COMPLETADO) {
        setResults({
          sentiment: job.sentiment,
          score: job.score,
          keywords: job.keywords,
        })
        setLoading(false)
        return true
      }
      return false
    }

    if (await check()) return

    const intervalId = setInterval(async () => {
      try {
        if (await check()) clearInterval(intervalId)
      } catch (err) {
        setError(err.message || 'Error al consultar el estado')
        setLoading(false)
        clearInterval(intervalId)
      }
    }, POLL_INTERVAL_MS)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setResults(null)
    setStatus(null)
    setJobId(null)

    try {
      // PASO 1 — Usuario envía texto → Python (pythonJobGateway.submitText)
      const response = await submitTextUseCase(pythonJobGateway, text)
      setJobId(response.jobId)
      setStatus(response.status)
      // PASO 5 — Inicia polling hasta que Java termine y Python devuelva COMPLETADO
      await pollUntilComplete(response.jobId)
    } catch (err) {
      setError(err.message || 'Error al enviar el texto')
      setLoading(false)
    }
  }

  return (
    <div className="app">
      <header className="header">
                <h1>AnalytiCore - Examen Final</h1>

        <p>Análisis </p>
      </header>

      <main className="main">
        <FrontendMonitor
          phase={phase}
          text={text}
          jobId={jobId}
          results={results}
          isRunning={loading}
          status={status}
        />

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

        <ResultsPanel results={results} loading={loading && status !== JobStatus.COMPLETADO} />
      </main>

      <footer className="footer">
        <p>Prototipo SOA — React + Python + Java + PostgreSQL</p>
      </footer>
    </div>
  )
}
