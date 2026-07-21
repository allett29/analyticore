/**
 * Capa de Aplicación — Cliente REST hacia el Servicio Python.
 *
 * BUS DE COMUNICACIÓN: REST/HTTP (fetch API del navegador)
 * URL base: variable VITE_API_URL (configurada en Render al hacer build del Docker)
 *
 * Este archivo es el ÚNICO punto del Frontend que se comunica con el backend.
 */
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8000'

/**
 * BUS REST → Python (presentation/routes.py):
 *   POST {VITE_API_URL}/api/jobs  body: { "text": "..." }
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
 * BUS REST → Python (presentation/routes.py):
 *   GET {VITE_API_URL}/api/jobs/{jobId}
 */
export async function getJobStatus(jobId) {
  const response = await fetch(`${API_BASE_URL}/api/jobs/${jobId}`)

  if (!response.ok) {
    throw new Error(`Error al consultar job: ${response.status}`)
  }

  return response.json()
}
