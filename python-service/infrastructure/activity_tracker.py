"""
Registro de actividad interna del Servicio Python — solo para el panel / .
No almacena estado de negocio (eso vive en PostgreSQL).
"""
import time
from threading import Lock

_lock = Lock()

# Pasos INTERNOS que ejecuta solo este servicio Python
INTERNAL_STEPS = [
    "En espera de solicitudes del Frontend",
    "Recibí POST /api/jobs desde el Frontend",
    "Validando el texto recibido",
    "Guardando job en PostgreSQL (estado PENDIENTE)",
    "Notificando al Servicio Java vía REST",
    "Java respondió — devolviendo jobId al Frontend",
    "Trabajo entregado correctamente",
]

_state = {
    "step": 0,
    "message": "En espera — listo para recibir textos del Frontend",
    "detail": "",
    "mode": "idle",  # idle | active | done
    "updated_at": time.time(),
}


def _set(step: int, message: str, detail: str = "", mode: str = "active"):
    with _lock:
        _state["step"] = step
        _state["message"] = message
        _state["detail"] = detail
        _state["mode"] = mode
        _state["updated_at"] = time.time()


def on_received(text: str):
    preview = text[:80] + "..." if len(text) > 80 else text
    _set(1, "Recibí texto del Frontend", f'Texto recibido: "{preview}"')


def on_validating():
    _set(2, "Validando formato y longitud del texto...")


def on_saving(job_id: str):
    _set(3, "Guardando en PostgreSQL...", f"Job ID: {job_id} | Estado: PENDIENTE")


def on_calling_java(job_id: str):
    _set(4, "Llamando al Servicio Java...", f"POST /api/analyze/{job_id}")


def on_finished(job_id: str):
    _set(6, "Proceso completado — jobId enviado al Frontend", f"Job ID: {job_id}", mode="done")


def get_status() -> dict:
    with _lock:
        step = _state["step"]
        mode = _state["mode"]
        steps = []
        for i, label in enumerate(INTERNAL_STEPS):
            if mode == "idle" and i == 0:
                status = "active"
            elif mode == "done" or i < step:
                status = "done"
            elif i == step:
                status = "active"
            else:
                status = "pending"
            steps.append({"text": label, "status": status})

        return {
            "service": "python",
            "message": _state["message"],
            "detail": _state["detail"],
            "mode": mode,
            "activeStep": step,
            "steps": steps,
        }
