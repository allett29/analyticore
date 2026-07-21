/**
 * Componente raíz de la SPA.
 * Orquesta la UI: formulario de texto, envío al Python y polling de resultados.
 */
import { useState } from 'react'
import TextAnalyzer from './components/TextAnalyzer'
import ResultsPanel from './components/ResultsPanel'
import FrontendMonitor from './components/FrontendMonitor'
import { submitText, getJobStatus } from './services/api'

const POLL_INTERVAL_MS = 2000

export default function App() {
  const [text, setText] = useState('')
  const [jobId, setJobId] = useState(null)
  const [status, setStatus] = useState(null)
  const [results, setResults] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  // Fase interna del Frontend: 0=espera, 1=preparando, 2=enviando, 3=polling, 4=resultado
  const [phase, setPhase] = useState(0)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setResults(null)
    setStatus(null)
    setJobId(null)
    setPhase(1)

    try {
      setPhase(2)
      const response = await submitText(text)
      setJobId(response.jobId)

      setPhase(3)
      const job = await getJobStatus(response.jobId)
      setStatus(job.status)

      if (job.status === 'COMPLETADO') {
        const res = { sentiment: job.sentiment, score: job.score, keywords: job.keywords }
        setResults(res)
        setPhase(4)
        setLoading(false)
        return
      }

      pollJobStatus(response.jobId)
    } catch (err) {
      setPhase(0)
      setError(err.message || 'Error al enviar el texto')
      setLoading(false)
    }
  }

  const pollJobStatus = (id) => {
    const intervalId = setInterval(async () => {
      try {
        const job = await getJobStatus(id)
        setStatus(job.status)

        if (job.status === 'COMPLETADO') {
          const res = { sentiment: job.sentiment, score: job.score, keywords: job.keywords }
          setResults(res)
          setPhase(4)
          setLoading(false)
          clearInterval(intervalId)
        }
      } catch (err) {
        setPhase(0)
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
        <FrontendMonitor
          phase={phase}
          text={text}
          jobId={jobId}
          results={results}
          isRunning={loading}
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

        <ResultsPanel results={results} loading={loading && status !== 'COMPLETADO'} />
      </main>

      <footer className="footer">
        <p>Prototipo SOA — React + Python + Java + PostgreSQL</p>
      </footer>
    </div>
  )
}
