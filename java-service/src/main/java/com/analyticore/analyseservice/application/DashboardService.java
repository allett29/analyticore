package com.analyticore.analyseservice.application;

import com.analyticore.analyseservice.domain.Job;
import com.analyticore.analyseservice.domain.JobStatus;
import com.analyticore.analyseservice.infrastructure.persistence.JobRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Estado del panel / leído desde PostgreSQL (stateless). */
@Service
public class DashboardService {

    private static final String[] STEPS = {
            "En espera de notificaciones de Python",
            "Job recibido desde Python (PENDIENTE)",
            "Estado actualizado a PROCESANDO",
            "Análisis de sentimiento en ejecución",
            "Extracción de palabras clave",
            "Resultados guardados (COMPLETADO)",
    };

    private final JobRepository jobRepository;

    public DashboardService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Map<String, Object> getStatus() {
        return jobRepository.findTopByOrderByCreatedAtDesc()
                .map(this::buildPayload)
                .orElseGet(this::idlePayload);
    }

    private Map<String, Object> idlePayload() {
        Map<String, Object> result = new HashMap<>();
        result.put("service", "java");
        result.put("message", "En espera — listo para recibir trabajos de Python");
        result.put("detail", "");
        result.put("mode", "idle");
        result.put("activeStep", 0);
        result.put("steps", List.of(Map.of("text", STEPS[0], "status", "active")));
        return result;
    }

    private Map<String, Object> buildPayload(Job job) {
        JobStatus status = job.getStatus();
        int step;
        String mode;
        String message;

        if (status == JobStatus.PENDIENTE) {
            step = 1;
            mode = "active";
            message = "Job en cola — estado PENDIENTE";
        } else if (status == JobStatus.PROCESANDO) {
            step = 3;
            mode = "active";
            message = "Analizando texto — estado PROCESANDO";
        } else {
            step = 5;
            mode = "done";
            message = "Análisis completado — estado COMPLETADO";
        }

        List<Map<String, String>> steps = new ArrayList<>();
        for (int i = 0; i < STEPS.length; i++) {
            String stepStatus;
            if (status == JobStatus.COMPLETADO || i < step) {
                stepStatus = "done";
            } else if (i == step) {
                stepStatus = "active";
            } else {
                stepStatus = "pending";
            }
            steps.add(Map.of("text", STEPS[i], "status", stepStatus));
        }

        String preview = job.getText().length() > 60
                ? job.getText().substring(0, 60) + "..." : job.getText();

        Map<String, Object> result = new HashMap<>();
        result.put("service", "java");
        result.put("message", message);
        result.put("detail", "Job ID: " + job.getId() + " | Texto: \"" + preview + "\"");
        result.put("mode", mode);
        result.put("activeStep", step);
        result.put("steps", steps);
        return result;
    }
}
