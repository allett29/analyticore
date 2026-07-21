/**
 * Componente raíz de la SPA.
 * Orquesta la UI: formulario de texto, envío al Python y polling de resultados.
 */
import { useState } from 'react'
import TextAnalyzer from './components/TextAnalyzer'
import ResultsPanel from './components/ResultsPanel'
import { submitText, getJobStatus } from './services/api'

const POLL_INTERVAL_MS = 2000

export default function App() {
  const [text, setText] = useState('')
  const [jobId, setJobId] = useState(null)
  const [status, setStatus] = useState(null)
  const [results, setResults] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  /**
   * Flujo paso 1 y 2: Usuario envía texto → Frontend llama POST /api/jobs (Python)
   * Python crea PENDIENTE, llama Java, devuelve jobId.
   */
  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setResults(null)
    setStatus(null)

    try {
      const response = await submitText(text)
      setJobId(response.jobId)
      setStatus(response.status)

      // Flujo paso 5: polling periódico con jobId hasta COMPLETADO
      pollJobStatus(response.jobId)
    } catch (err) {
      setError(err.message || 'Error al enviar el texto')
      setLoading(false)
    }
  }

  /**
   * Consulta GET /api/jobs/{jobId} cada 2 segundos.
   * Se detiene cuando el estado es COMPLETADO.
   */
  const pollJobStatus = (id) => {
    const intervalId = setInterval(async () => {
      try {
        const job = await getJobStatus(id)
        setStatus(job.status)

        if (job.status === 'COMPLETADO') {
          setResults({
            sentiment: job.sentiment,
            score: job.score,
            keywords: job.keywords,
          })
          setLoading(false)
          clearInterval(intervalId)
        }
      } catch (err) {
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
