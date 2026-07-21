package com.analyticore.analyseservice.presentation;

import com.analyticore.analyseservice.application.AnalysisService;
import com.analyticore.analyseservice.application.AnalysisWorker;
import com.analyticore.analyseservice.domain.Job;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ═══ RECIBE del Servicio Python — PASO 3 ═══
 *
 * Origen: python-service/infrastructure/java_client.py (POST /api/analyze/{jobId})
 *
 * Flujo interno:
 *   1. startAnalysis()  → PostgreSQL: estado PROCESANDO (respuesta síncrona a Python)
 *   2. runAnalysis()    → worker asíncrono → AnalysisService.completeAnalysis()
 *                         → PostgreSQL: sentimiento, keywords, estado COMPLETADO
 *   3. Frontend consulta resultados vía Python (PASO 5, polling)
 */
@RestController
@RequestMapping("/api")
public class AnalysisController {

    private final AnalysisService analysisService;
    private final AnalysisWorker analysisWorker;

    public AnalysisController(AnalysisService analysisService, AnalysisWorker analysisWorker) {
        this.analysisService = analysisService;
        this.analysisWorker = analysisWorker;
    }

    /**
     * RECIBE PASO 3 ← Python (HttpJavaAnalysisClient.trigger_analysis).
     * Marca PROCESANDO en PostgreSQL y lanza el análisis en segundo plano.
     * Responde 202 Accepted a Python (no espera a que termine el análisis).
     */
    @PostMapping("/analyze/{jobId}")
    public ResponseEntity<Map<String, Object>> analyze(@PathVariable UUID jobId) {
        // PASO 4a — Actualiza PostgreSQL: PENDIENTE → PROCESANDO
        Job job = analysisService.startAnalysis(jobId);
        // PASO 4b — Análisis asíncrono → guarda resultados COMPLETADO en PostgreSQL
        analysisWorker.runAnalysis(jobId);
        return ResponseEntity.accepted().body(Map.of(
                "jobId", job.getId().toString(),
                "status", job.getStatus().name()
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "java-analysis"));
    }
}
