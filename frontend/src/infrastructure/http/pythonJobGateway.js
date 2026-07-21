/**
 * Adaptador de infraestructura — implementa JobGatewayPort vía REST/fetch.
 */
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8000'

/** @type {import('../../domain/ports/jobGatewayPort.js').JobGatewayPort} */
export const pythonJobGateway = {
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

    return response.json()
  },

  async getJobStatus(jobId) {
    const response = await fetch(`${API_BASE_URL}/api/jobs/${jobId}`)

    if (!response.ok) {
      throw new Error(`Error al consultar job: ${response.status}`)
    }

    return response.json()
  },
}
