/**
 * Casos de uso — dependen del puerto JobGatewayPort, no de fetch/HTTP.
 */

/** @param {import('../domain/ports/jobGatewayPort.js').JobGatewayPort} gateway */
export async function submitTextUseCase(gateway, text) {
  return gateway.submitText(text)
}

/** @param {import('../domain/ports/jobGatewayPort.js').JobGatewayPort} gateway */
export async function getJobStatusUseCase(gateway, jobId) {
  return gateway.getJobStatus(jobId)
}
