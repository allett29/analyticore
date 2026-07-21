/**
 * Puerto de dominio — contrato de comunicación con el Servicio Python.
 * La capa de aplicación depende de esta interfaz, no de fetch ni HTTP.
 *
 * @typedef {Object} JobGatewayPort
 * @property {(text: string) => Promise<{jobId: string, status: string}>} submitText
 * @property {(jobId: string) => Promise<Object>} getJobStatus
 */
