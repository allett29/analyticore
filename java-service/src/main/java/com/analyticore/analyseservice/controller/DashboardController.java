package com.analyticore.analyseservice.controller;

import com.analyticore.analyseservice.service.DashboardService;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Panel de monitoreo del Servicio Java.
 * GET /       → página HTML animada (abrir en el navegador)
 * GET /api/status → JSON consultado por el panel cada segundo
 */
@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/api/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(dashboardService.getStatus());
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok(DASHBOARD_HTML);
    }

    private static final String DASHBOARD_HTML = """
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>AnalytiCore — Servicio de Análisis</title>
  <style>
    *{margin:0;padding:0;box-sizing:border-box}
    body{font-family:'Segoe UI',system-ui,sans-serif;background:#0f172a;color:#e2e8f0;min-height:100vh;padding:2rem}
    .header{text-align:center;margin-bottom:2rem}
    .badge-svc{display:inline-block;background:#f9731622;border:1px solid #f97316;color:#f97316;
      padding:.3rem .9rem;border-radius:999px;font-size:.8rem;font-weight:600;margin-bottom:.5rem}
    h1{font-size:1.6rem;color:#f1f5f9}
    .tech{color:#64748b;font-size:.9rem;margin-top:.3rem}
    .status-box{background:#1e293b;border:1px solid #334155;border-radius:12px;padding:1.2rem;margin-bottom:1.5rem}
    .status-box.active{border-color:#f97316;box-shadow:0 0 20px #f9731633}
    .status-label{font-size:.75rem;text-transform:uppercase;letter-spacing:.05em;color:#64748b;margin-bottom:.4rem}
    .status-msg{font-size:1rem;color:#e2e8f0}
    .job-info{margin-top:.6rem;font-size:.8rem;color:#94a3b8;font-family:monospace}
    .step{display:flex;align-items:center;gap:.8rem;padding:.7rem 1rem;border-radius:10px;
      border:1px solid #334155;background:#0f172a;transition:all .4s;margin-bottom:0}
    .step.mine{border-left:3px solid #f97316}
    .step.active{border-color:#f97316;background:#2a1a0f;box-shadow:0 0 15px #f9731622;transform:scale(1.01)}
    .step.done{border-color:#166534;background:#0f1f17;opacity:.8}
    .step.idle{opacity:.4}
    .step-icon{width:36px;height:36px;border-radius:8px;background:#334155;
      display:flex;align-items:center;justify-content:center;font-size:1.1rem;flex-shrink:0}
    .step.active .step-icon{background:#f9731633}
    .step.done .step-icon{background:#166534}
    .step-name{font-weight:600;font-size:.9rem}
    .step-action{font-size:.75rem;color:#94a3b8;margin-top:.1rem}
    .connector{height:20px;margin-left:1.65rem;position:relative}
    .connector-line{width:2px;height:100%;background:#334155;margin:0 auto}
    .connector.active .connector-line{background:linear-gradient(180deg,#f97316,#4ade80)}
    .dot{position:absolute;top:0;left:50%;transform:translateX(-50%);width:7px;height:7px;
      border-radius:50%;background:#f97316;animation:travel .7s ease-in-out infinite}
    @keyframes travel{0%{top:0;opacity:1}100%{top:100%;opacity:0}}
    @keyframes pulse{0%,100%{opacity:1}50%{opacity:.5}}
    .pulse{animation:pulse 1.2s ease-in-out infinite}
    .footer{text-align:center;margin-top:2rem;color:#475569;font-size:.75rem}
  </style>
</head>
<body>
  <div class="header">
    <div class="badge-svc">☕ ESTE SERVICIO</div>
    <h1>Servicio de Análisis</h1>
    <p class="tech">Java · Spring Boot</p>
  </div>
  <div class="status-box" id="statusBox">
    <div class="status-label">Estado actual</div>
    <div class="status-msg pulse" id="statusMsg">Conectando...</div>
    <div class="job-info" id="jobInfo"></div>
  </div>
  <div id="flow"></div>
  <div class="footer">Actualizando cada 1s desde PostgreSQL · AnalytiCore SOA</div>
  <script>
    const STEPS = [
      {id:0,name:'Usuario',       action:'Escribe el texto',               icon:'👤',mine:false},
      {id:1,name:'Frontend',      action:'POST /api/jobs → Python',        icon:'⚛️',mine:false},
      {id:2,name:'Python',        action:'Valida el texto recibido',       icon:'🐍',mine:false},
      {id:3,name:'PostgreSQL',    action:'INSERT estado PENDIENTE',        icon:'🗄️',mine:false},
      {id:4,name:'Python → Java', action:'POST /api/analyze/{jobId}',      icon:'🔗',mine:false},
      {id:5,name:'Java',          action:'Analiza sentimiento + keywords',  icon:'☕',mine:true},
      {id:6,name:'PostgreSQL',    action:'UPDATE estado COMPLETADO',       icon:'🗄️',mine:true},
      {id:7,name:'Frontend',      action:'Muestra resultados al usuario',  icon:'✅',mine:false},
    ];
    function renderFlow(globalStep){
      document.getElementById('flow').innerHTML=STEPS.map((s,i)=>{
        let cls='idle';
        if(globalStep<0) cls=s.mine?'mine idle':'idle';
        else if(s.id<globalStep) cls=s.mine?'mine done':'done';
        else if(s.id===globalStep) cls=s.mine?'mine active':'active';
        else cls=s.mine?'mine idle':'idle';
        const conn=i<STEPS.length-1?`<div class="connector ${s.id<globalStep||s.id===globalStep?'active':''}">
          <div class="connector-line"></div>${s.id===globalStep?'<div class="dot"></div>':''}</div>`:'';
        return `<div class="step ${cls}"><div class="step-icon">${s.id<globalStep?'✓':s.icon}</div>
          <div><div class="step-name">${s.name}</div><div class="step-action">${s.action}</div></div></div>${conn}`;
      }).join('');
    }
    async function poll(){
      try{
        const d=await(await fetch('/api/status')).json();
        document.getElementById('statusMsg').textContent=d.message;
        document.getElementById('statusMsg').className=d.state==='active'?'status-msg pulse':'status-msg';
        document.getElementById('statusBox').className='status-box'+(d.state==='active'?' active':'');
        document.getElementById('jobInfo').textContent=d.jobId
          ?`Job: ${d.jobId} | Estado BD: ${d.status} | Texto: "${d.textPreview}"`:'Sin jobs registrados aún';
        renderFlow(d.globalStep);
      }catch(e){document.getElementById('statusMsg').textContent='Error conectando al servicio';}
    }
    poll();setInterval(poll,1000);
  </script>
</body>
</html>""";
}
