"""
Panel de monitoreo del Servicio Python.
Muestra en el navegador en qué paso del flujo SOA está el proceso.
Lee el estado desde PostgreSQL (servicio stateless).
"""
from fastapi import APIRouter
from fastapi.responses import HTMLResponse

from infrastructure.database import JobRepository

router = APIRouter()
job_repository = JobRepository()

# Pasos que ejecuta ESTE servicio (Python) — resaltados en el diagrama
MY_STEPS = {2, 3, 4}


def _build_status():
    """Consulta el último job en BD y determina el paso activo global."""
    job = job_repository.find_latest()
    if not job:
        return {
            "service": "python",
            "state": "idle",
            "message": "En espera — listo para recibir textos del Frontend",
            "globalStep": -1,
            "jobId": None,
            "status": None,
            "textPreview": None,
        }

    text_preview = job.text[:60] + "..." if len(job.text) > 60 else job.text
    status = job.status.value

    if status == "PENDIENTE":
        return {
            "service": "python",
            "state": "active",
            "message": "Validando texto, guardando PENDIENTE y llamando a Java...",
            "globalStep": 3,
            "jobId": str(job.id),
            "status": status,
            "textPreview": text_preview,
        }
    if status == "PROCESANDO":
        return {
            "service": "python",
            "state": "done",
            "message": "Job enviado a Java — esperando análisis",
            "globalStep": 5,
            "jobId": str(job.id),
            "status": status,
            "textPreview": text_preview,
        }
    return {
        "service": "python",
        "state": "done",
        "message": f"Último job completado — sentimiento: {job.sentiment or 'N/A'}",
        "globalStep": 7,
        "jobId": str(job.id),
        "status": status,
        "textPreview": text_preview,
    }


@router.get("/api/status")
def get_status():
    """API JSON consultada por el panel HTML cada segundo."""
    return _build_status()


@router.get("/", response_class=HTMLResponse)
def dashboard():
    """
    Panel visual en el navegador para el Servicio de Submisión (Python).
    Abre esta URL en una pestaña separada para ver el proceso en tiempo real.
    """
    return HTMLResponse(content=_render_html(
        service_name="Servicio de Submisión",
        service_tech="Python · FastAPI",
        service_icon="🐍",
        accent_color="#4ade80",
        my_steps=MY_STEPS,
    ))


def _render_html(service_name, service_tech, service_icon, accent_color, my_steps):
    my_steps_json = list(my_steps)
    return f"""<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>AnalytiCore — {service_name}</title>
  <style>
    *{{margin:0;padding:0;box-sizing:border-box}}
    body{{font-family:'Segoe UI',system-ui,sans-serif;background:#0f172a;color:#e2e8f0;min-height:100vh;padding:2rem}}
    .header{{text-align:center;margin-bottom:2rem}}
    .badge-svc{{display:inline-block;background:{accent_color}22;border:1px solid {accent_color};color:{accent_color};
      padding:.3rem .9rem;border-radius:999px;font-size:.8rem;font-weight:600;margin-bottom:.5rem}}
    h1{{font-size:1.6rem;color:#f1f5f9}}
    .tech{{color:#64748b;font-size:.9rem;margin-top:.3rem}}
    .status-box{{background:#1e293b;border:1px solid #334155;border-radius:12px;padding:1.2rem;margin-bottom:1.5rem}}
    .status-box.active{{border-color:{accent_color};box-shadow:0 0 20px {accent_color}33}}
    .status-label{{font-size:.75rem;text-transform:uppercase;letter-spacing:.05em;color:#64748b;margin-bottom:.4rem}}
    .status-msg{{font-size:1rem;color:#e2e8f0}}
    .job-info{{margin-top:.6rem;font-size:.8rem;color:#94a3b8;font-family:monospace}}
    .flow{{display:flex;flex-direction:column;gap:0}}
    .step{{display:flex;align-items:center;gap:.8rem;padding:.7rem 1rem;border-radius:10px;
      border:1px solid #334155;background:#0f172a;transition:all .4s}}
    .step.mine{{border-left:3px solid {accent_color}}}
    .step.active{{border-color:{accent_color};background:#1a2744;box-shadow:0 0 15px {accent_color}22;transform:scale(1.01)}}
    .step.done{{border-color:#166534;background:#0f1f17;opacity:.8}}
    .step.idle{{opacity:.4}}
    .step-icon{{width:36px;height:36px;border-radius:8px;background:#334155;
      display:flex;align-items:center;justify-content:center;font-size:1.1rem;flex-shrink:0}}
    .step.active .step-icon{{background:{accent_color}33}}
    .step.done .step-icon{{background:#166534}}
    .step-body{{flex:1}}
    .step-name{{font-weight:600;font-size:.9rem}}
    .step-action{{font-size:.75rem;color:#94a3b8;margin-top:.1rem}}
    .connector{{height:20px;margin-left:1.65rem;position:relative}}
    .connector-line{{width:2px;height:100%;background:#334155;margin:0 auto}}
    .connector.active .connector-line{{background:linear-gradient(180deg,#4ade80,#38bdf8)}}
    .dot{{position:absolute;top:0;left:50%;transform:translateX(-50%);width:7px;height:7px;
      border-radius:50%;background:{accent_color};animation:travel .7s ease-in-out infinite}}
    @keyframes travel{{0%{{top:0;opacity:1}}100%{{top:100%;opacity:0}}}}
    @keyframes pulse{{0%,100%{{opacity:1}}50%{{opacity:.5}}}}
    .pulse{{animation:pulse 1.2s ease-in-out infinite}}
    .footer{{text-align:center;margin-top:2rem;color:#475569;font-size:.75rem}}
  </style>
</head>
<body>
  <div class="header">
    <div class="badge-svc">{service_icon} ESTE SERVICIO</div>
    <h1>{service_name}</h1>
    <p class="tech">{service_tech}</p>
  </div>
  <div class="status-box" id="statusBox">
    <div class="status-label">Estado actual</div>
    <div class="status-msg pulse" id="statusMsg">Conectando...</div>
    <div class="job-info" id="jobInfo"></div>
  </div>
  <div class="flow" id="flow"></div>
  <div class="footer">Actualizando cada 1s desde PostgreSQL · AnalytiCore SOA</div>
  <script>
    const MY_STEPS = {my_steps_json};
    const ACCENT = '{accent_color}';
    const STEPS = [
      {{id:0, name:'Usuario',       action:'Escribe el texto',              icon:'👤', mine:false}},
      {{id:1, name:'Frontend',      action:'POST /api/jobs → Python',       icon:'⚛️', mine:false}},
      {{id:2, name:'Python',        action:'Valida el texto recibido',      icon:'🐍', mine:true}},
      {{id:3, name:'PostgreSQL',    action:'INSERT estado PENDIENTE',       icon:'🗄️', mine:true}},
      {{id:4, name:'Python → Java', action:'POST /api/analyze/{{jobId}}',   icon:'🔗', mine:true}},
      {{id:5, name:'Java',          action:'Analiza sentimiento + keywords',icon:'☕', mine:false}},
      {{id:6, name:'PostgreSQL',    action:'UPDATE estado COMPLETADO',      icon:'🗄️', mine:false}},
      {{id:7, name:'Frontend',      action:'Muestra resultados al usuario', icon:'✅', mine:false}},
    ];

    function renderFlow(globalStep) {{
      const flow = document.getElementById('flow');
      flow.innerHTML = STEPS.map((s, i) => {{
        let cls = 'idle';
        if (globalStep < 0) cls = s.mine ? 'mine idle' : 'idle';
        else if (s.id < globalStep) cls = s.mine ? 'mine done' : 'done';
        else if (s.id === globalStep) cls = s.mine ? 'mine active' : 'active';
        else cls = s.mine ? 'mine idle' : 'idle';
        const conn = i < STEPS.length-1
          ? `<div class="connector ${{s.id < globalStep || s.id === globalStep ? 'active' : ''}}">
               <div class="connector-line"></div>
               ${{s.id === globalStep ? '<div class="dot"></div>' : ''}}
             </div>` : '';
        return `<div class="step ${{cls}}">
          <div class="step-icon">${{s.id < globalStep ? '✓' : s.icon}}</div>
          <div class="step-body">
            <div class="step-name">${{s.name}}</div>
            <div class="step-action">${{s.action}}</div>
          </div>
        </div>${{conn}}`;
      }}).join('');
    }}

    async function poll() {{
      try {{
        const r = await fetch('/api/status');
        const d = await r.json();
        const box = document.getElementById('statusBox');
        const msg = document.getElementById('statusMsg');
        const info = document.getElementById('jobInfo');
        msg.textContent = d.message;
        msg.className = d.state === 'active' ? 'status-msg pulse' : 'status-msg';
        box.className = 'status-box' + (d.state === 'active' ? ' active' : '');
        info.textContent = d.jobId
          ? `Job: ${{d.jobId}} | Estado BD: ${{d.status}} | Texto: "${{d.textPreview}}"`
          : 'Sin jobs registrados aún';
        renderFlow(d.globalStep);
      }} catch(e) {{
        document.getElementById('statusMsg').textContent = 'Error conectando al servicio';
      }}
    }}
    poll();
    setInterval(poll, 1000);
  </script>
</body>
</html>"""
