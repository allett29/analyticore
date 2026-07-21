package com.analyticore.analyseservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registro de actividad interna del Servicio Java — solo para el panel / .
 * No almacena estado de negocio (eso vive en PostgreSQL).
 */
public class ActivityTracker {

    private static final String[] INTERNAL_STEPS = {
            "En espera de notificaciones de Python",
            "Recibí POST /api/analyze/{jobId} desde Python",
            "Leyendo el texto del job en PostgreSQL",
            "Actualizando estado → PROCESANDO",
            "Ejecutando análisis de sentimiento",
            "Extrayendo palabras clave del texto",
            "Guardando resultados en PostgreSQL (COMPLETADO)",
            "Análisis finalizado — listo para consulta",
    };

    private static int step = 0;
    private static String message = "En espera — listo para recibir trabajos de Python";
    private static String detail = "";
    private static String mode = "idle";

    private ActivityTracker() {}

    public static void onReceived(String jobId) {
        step = 1;
        message = "Recibí solicitud de análisis desde Python";
        detail = "Job ID: " + jobId;
        mode = "active";
    }

    public static void onReadingDb(String textPreview) {
        step = 2;
        message = "Leyendo texto desde PostgreSQL...";
        detail = "Texto: \"" + textPreview + "\"";
        mode = "active";
    }

    public static void onProcessing() {
        step = 3;
        message = "Actualizando estado del job a PROCESANDO...";
        detail = "UPDATE jobs SET status = 'PROCESANDO'";
        mode = "active";
    }

    public static void onAnalyzingSentiment() {
        step = 4;
        message = "Analizando sentimiento del texto...";
        detail = "Contando palabras positivas y negativas";
        mode = "active";
    }

    public static void onExtractingKeywords() {
        step = 5;
        message = "Extrayendo palabras clave...";
        detail = "Tokenizando y filtrando stopwords";
        mode = "active";
    }

    public static void onSaving(String sentiment, String keywords) {
        step = 6;
        message = "Guardando resultados en PostgreSQL...";
        detail = "Sentimiento: " + sentiment + " | Keywords: " + keywords;
        mode = "active";
    }

    public static void onFinished(String sentiment, double score) {
        step = 7;
        message = "Análisis completado exitosamente";
        detail = "Resultado: " + sentiment + " (score: " + String.format("%.2f", score) + ")";
        mode = "done";
    }

    public static Map<String, Object> getStatus() {
        List<Map<String, String>> steps = new ArrayList<>();
        for (int i = 0; i < INTERNAL_STEPS.length; i++) {
            String status;
            if ("idle".equals(mode) && i == 0) {
                status = "active";
            } else if ("done".equals(mode) || i < step) {
                status = "done";
            } else if (i == step) {
                status = "active";
            } else {
                status = "pending";
            }
            steps.add(Map.of("text", INTERNAL_STEPS[i], "status", status));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("service", "java");
        result.put("message", message);
        result.put("detail", detail);
        result.put("mode", mode);
        result.put("activeStep", step);
        result.put("steps", steps);
        return result;
    }
}
