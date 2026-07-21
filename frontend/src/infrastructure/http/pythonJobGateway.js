/**
 * ═══ INICIO DEL FLUJO REST (Frontend → Python) ═══
 *
 * Este es el ÚNICO archivo del Frontend que habla con otro microservicio.
 * Implementa JobGatewayPort usando fetch/HTTP.
 *
 * Destino: Servicio Python (FastAPI)
 * Recibe en: python-service/presentation/routes.py
 */
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8000'

/** @type {import('../../domain/ports/jobGatewayPort.js').JobGatewayPort} */
export const pythonJobGateway = {
  /**
   * PASO 1 — Envía el texto del usuario al Servicio Python.
   * POST {VITE_API_URL}/api/jobs  →  routes.py submit_job()
   * Python guardará el job como PENDIENTE y notificará a Java.
   */
  async submitText(text) {
    const response = await fetch(`${API_BASE_URL}/api/jobs`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text }),
    })

    if (!response.ok) {
      const error = await response.json().catch(() => ({}))
      throw new Error(error.detail || `Error ${response.status}`)
    }

    return response.json() // { jobId, status }
  },

  /**
   * PASO 5 — Polling: consulta estado y resultados al Servicio Python.
   * GET {VITE_API_URL}/api/jobs/{jobId}  →  routes.py get_job()
   * Python lee de PostgreSQL (donde Java guardó los resultados).
   * Se repite cada 1s hasta que status === COMPLETADO.
   */
  async getJobStatus(jobId) {
    const response = await fetch(`${API_BASE_URL}/api/jobs/${jobId}`)

    if (!response.ok) {
      throw new Error(`Error al consultar job: ${response.status}`)
    }

    return response.json() // { jobId, status, sentiment, score, keywords }
  },
}
