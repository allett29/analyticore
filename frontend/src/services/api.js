/**
 * Capa de Aplicación/Servicios: comunicación REST con el Servicio Python.
 * Toda la comunicación Frontend ↔ Backend es exclusivamente vía API REST.
 *
 * VITE_API_URL se configura en build time (Docker/Render).
 * En local: http://localhost:8000
 * En Render: URL pública del python-service
 */
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8000'

/**
 * POST /api/jobs — envía texto al Servicio de Submisión (Python).
 * Retorna { jobId, status }.
 */
export async function submitText(text) {
  const response = await fetch(`${API_BASE_URL}/api/jobs`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ text }),
  })

  if (!response.ok) {
    const error = await response.json().catch(() => ({}))
    throw new Error(error.detail || `Error ${response.status}`)
  }

  return response.json()
}

/**
 * GET /api/jobs/{jobId} — consulta estado y resultados (polling).
 * Retorna { jobId, status, sentiment, score, keywords }.
 */
export async function getJobStatus(jobId) {
  const response = await fetch(`${API_BASE_URL}/api/jobs/${jobId}`)

  if (!response.ok) {
    throw new Error(`Error al consultar job: ${response.status}`)
  }

  return response.json()
}
