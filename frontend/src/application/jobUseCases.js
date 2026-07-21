/**
 * Casos de uso — puente entre App.jsx y el gateway.
 * No hace HTTP directo; delega en pythonJobGateway.js.
 */

/** Dispara PASO 1 → pythonJobGateway.submitText() → Python */
export async function submitTextUseCase(gateway, text) {
  return gateway.submitText(text)
}

/** Dispara PASO 5 → pythonJobGateway.getJobStatus() → Python (polling) */
export async function getJobStatusUseCase(gateway, jobId) {
  return gateway.getJobStatus(jobId)
}
