package com.analyticore.analyseservice.controller;

import com.analyticore.analyseservice.domain.Job;
import com.analyticore.analyseservice.service.AnalysisService;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Capa de Presentación: API REST interna expuesta al Servicio Python.
 * POST /api/analyze/{jobId} — punto de entrada cuando Python orquesta el análisis.
 */
@RestController
@RequestMapping("/api")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /**
     * Recibe notificación del Servicio Python para procesar un job.
     * Java lee el texto desde PostgreSQL, analiza y persiste resultados.
     */
    @PostMapping("/analyze/{jobId}")
    public ResponseEntity<Map<String, Object>> analyze(@PathVariable UUID jobId) {
        Job job = analysisService.analyzeJob(jobId);
        return ResponseEntity.ok(Map.of(
                "jobId", job.getId().toString(),
                "status", job.getStatus().name(),
                "sentiment", job.getSentiment(),
                "score", job.getScore()
        ));
    }

    /** Endpoint de salud para Render y monitoreo */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "java-analysis"));
    }
}
