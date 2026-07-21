/**
 * Capa de Presentación — Visualización de resultados del análisis.
 *
 * Comunicación:
 *   Recibe datos de App.jsx (que los obtuvo vía polling de Python → api.js getJobStatus)
 *   Origen de los datos: PostgreSQL tabla 'jobs' (escritos por Java, leídos por Python)
 *
 * No hace llamadas HTTP — solo renderiza el estado que recibe como prop.
 */
export default function ResultsPanel({ results, loading }) {
  if (loading) {
    return (
      <div className="results loading-state">
        <div className="spinner" />
        <p>Procesando análisis...</p>
      </div>
    )
  }

  if (!results) return null

  return (
    <div className="results">
      <h2>Resultados del análisis</h2>

      <div className="result-card">
        <h3>Sentimiento</h3>
        <p className={`sentiment sentiment-${results.sentiment?.toLowerCase()}`}>
          {results.sentiment}
        </p>
        <p className="score">Puntuación: {results.score?.toFixed(2)}</p>
      </div>

      <div className="result-card">
        <h3>Palabras clave</h3>
        <div className="keywords">
          {results.keywords?.map((kw, i) => (
            <span key={i} className="keyword-tag">{kw}</span>
          ))}
        </div>
      </div>
    </div>
  )
}
