package com.analyticore.analyseservice.service;

import com.analyticore.analyseservice.domain.Job;
import com.analyticore.analyseservice.domain.JobStatus;
import com.analyticore.analyseservice.repository.JobRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Lógica del panel de monitoreo del Servicio Java.
 * Lee el último job desde PostgreSQL (stateless).
 */
@Service
public class DashboardService {

    private final JobRepository jobRepository;

    public DashboardService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Map<String, Object> getStatus() {
        return jobRepository.findTopByOrderByCreatedAtDesc()
                .map(this::buildStatusFromJob)
                .orElseGet(this::buildIdleStatus);
    }

    private Map<String, Object> buildIdleStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "java");
        status.put("state", "idle");
        status.put("message", "En espera — listo para recibir trabajos de Python");
        status.put("globalStep", -1);
        status.put("jobId", null);
        status.put("status", null);
        status.put("textPreview", null);
        return status;
    }

    private Map<String, Object> buildStatusFromJob(Job job) {
        String textPreview = job.getText().length() > 60
                ? job.getText().substring(0, 60) + "..."
                : job.getText();

        Map<String, Object> status = new HashMap<>();
        status.put("jobId", job.getId().toString());
        status.put("status", job.getStatus().name());
        status.put("textPreview", textPreview);
        status.put("service", "java");

        if (job.getStatus() == JobStatus.PROCESANDO) {
            status.put("state", "active");
            status.put("message", "Analizando sentimiento y extrayendo palabras clave...");
            status.put("globalStep", 5);
        } else if (job.getStatus() == JobStatus.COMPLETADO) {
            status.put("state", "done");
            status.put("message", "Análisis completado — sentimiento: " + job.getSentiment());
            status.put("globalStep", 7);
        } else {
            // PENDIENTE: Python aún no ha llamado o está en camino
            status.put("state", "idle");
            status.put("message", "Esperando que Python envíe un nuevo trabajo...");
            status.put("globalStep", 4);
        }

        return status;
    }
}
