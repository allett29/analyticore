package com.analyticore.analyseservice.application;

import java.util.UUID;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Worker asíncrono — completa el análisis sin bloquear la respuesta REST a Python.
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
