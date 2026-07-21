package com.analyticore.analyseservice.controller;

import com.analyticore.analyseservice.domain.Job;
import com.analyticore.analyseservice.service.AnalysisService;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Capa de Presentación — API REST interna del Servicio Java.
 *
 * BUS ENTRANTE (quién llama a este controlador):
 *   Python → infrastructure/java_client.py línea 27
 *     POST {JAVA_SERVICE_URL}/api/analyze/{jobId}
 *
 * BUS SALIENTE (a quién delega):
 *   service/AnalysisService.java analyzeJob() → repository/JobRepository.java → PostgreSQL
 */
@RestController
@RequestMapping("/api")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /**
     * Endpoint del bus REST interno Python → Java.
     *
     * Llamado por: python-service/infrastructure/java_client.py línea 27 (httpx.post)
     * Delega a:    service/AnalysisService.java línea 53 (analyzeJob)
     * Responde:    JSON { jobId, status, sentiment, score } a Python (llamada síncrona)
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

    /** Health check para Render — sin comunicación con otros servicios. */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "java-analysis"));
    }
}
