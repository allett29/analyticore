package com.analyticore.analyseservice.controller;

import com.analyticore.analyseservice.service.DashboardService;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Panel de monitoreo INTERNO del Servicio Java.
 * Muestra únicamente lo que hace Java — no el flujo de otros servicios.
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
        return ResponseEntity.ok(HTML);
    }

    private static final String HTML = """
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8"/><meta name="viewport" content="width=device-width,initial-scale=1"/>
<title>AnalytiCore — Java</title>
<style>
*{margin:0;padding:0;box-sizing:border-box}
body{font-family:'Segoe UI',system-ui,sans-serif;background:#0f172a;color:#e2e8f0;min-height:100vh;padding:2rem;max-width:600px;margin:0 auto}
.hdr{text-align:center;margin-bottom:1.5rem}
.badge{display:inline-block;background:#f9731622;border:1px solid #f97316;color:#f97316;
  padding:.3rem .9rem;border-radius:999px;font-size:.8rem;font-weight:600}
h1{font-size:1.4rem;margin-top:.5rem;color:#f1f5f9}
.sub{color:#64748b;font-size:.85rem;margin-top:.25rem}
.live{background:#1e293b;border:1px solid #334155;border-radius:12px;padding:1rem;margin-bottom:1.25rem}
.live.active{border-color:#f97316;box-shadow:0 0 18px #f9731633}
.live-lbl{font-size:.7rem;text-transform:uppercase;letter-spacing:.06em;color:#64748b;margin-bottom:.3rem}
.live-msg{font-size:.95rem;color:#e2e8f0}
.live-detail{margin-top:.5rem;font-size:.8rem;color:#94a3b8;font-family:monospace;word-break:break-all}
.step{display:flex;gap:.75rem;align-items:flex-start;padding:.65rem .85rem;border-radius:9px;
  border:1px solid #1e293b;margin-bottom:.4rem;transition:all .35s}
.step.pending{opacity:.3}
.step.active{border-color:#f97316;background:#2a1508;animation:glow 1.2s ease-in-out infinite}
.step.done{border-color:#166534;background:#0a1f12;opacity:.85}
.step-num{width:26px;height:26px;border-radius:50%;background:#334155;display:flex;
  align-items:center;justify-content:center;font-size:.75rem;font-weight:700;flex-shrink:0;margin-top:.1rem}
.step.active .step-num{background:#f9731633;color:#f97316}
.step.done .step-num{background:#166534;color:#bbf7d0}
.step-text{font-size:.85rem;line-height:1.4;padding-top:.15rem}
@keyframes glow{0%,100%{box-shadow:0 0 8px #f9731622}50%{box-shadow:0 0 16px #f9731644}}
.pulse{animation:pulse 1s ease-in-out infinite}
@keyframes pulse{0%,100%{opacity:1}50%{opacity:.6}}
.foot{text-align:center;color:#475569;font-size:.72rem;margin-top:1.5rem}
</style>
</head>
<body>
<div class="hdr">
  <div class="badge">☕ SERVICIO JAVA</div>
  <h1>Servicio de Análisis</h1>
  <p class="sub">Spring Boot · Analiza y persiste resultados</p>
</div>
<div class="live" id="live">
  <div class="live-lbl">Proceso actual</div>
  <div class="live-msg pulse" id="msg">Conectando...</div>
  <div class="live-detail" id="detail"></div>
</div>
<div id="steps"></div>
<p class="foot">Solo procesos internos de Java · actualiza cada 500ms</p>
<script>
async function poll(){
  try{
    const d=await(await fetch('/api/status')).json();
    const live=document.getElementById('live');
    document.getElementById('msg').textContent=d.message;
    document.getElementById('msg').className=d.mode==='active'?'live-msg pulse':'live-msg';
    live.className='live'+(d.mode==='active'?' active':'');
    document.getElementById('detail').textContent=d.detail||'';
    document.getElementById('steps').innerHTML=d.steps.map((s,i)=>
      `<div class="step ${s.status}">
        <div class="step-num">${s.status==='done'?'✓':i+1}</div>
        <div class="step-text">${s.text}</div>
      </div>`).join('');
  }catch(e){document.getElementById('msg').textContent='Error de conexión';}
}
poll();setInterval(poll,500);
</script>
</body>
</html>""";
}
