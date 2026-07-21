package com.analyticore.analyseservice.application;

import java.util.UUID;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * PASO 4b — Worker asíncrono lanzado desde AnalysisController.
 * Ejecuta completeAnalysis() sin bloquear la respuesta 202 a Python.
 * Al terminar, PostgreSQL queda en COMPLETADO y el Frontend lo ve en el polling (PASO 5).
 */
@Service
public class AnalysisWorker {

    private final AnalysisService analysisService;

    public AnalysisWorker(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @Async
    public void runAnalysis(UUID jobId) {
        analysisService.completeAnalysis(jobId);
    }
}
