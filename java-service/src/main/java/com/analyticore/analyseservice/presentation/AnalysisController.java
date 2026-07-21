package com.analyticore.analyseservice.presentation;

import com.analyticore.analyseservice.application.AnalysisService;
import com.analyticore.analyseservice.application.AnalysisWorker;
import com.analyticore.analyseservice.domain.Job;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API REST interna — Python notifica que un job está listo para analizar.
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

    @PostMapping("/analyze/{jobId}")
    public ResponseEntity<Map<String, Object>> analyze(@PathVariable UUID jobId) {
        Job job = analysisService.startAnalysis(jobId);
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
