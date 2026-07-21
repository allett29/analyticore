package com.analyticore.analyseservice.service;

import com.analyticore.analyseservice.domain.Job;
import com.analyticore.analyseservice.domain.JobStatus;
import com.analyticore.analyseservice.repository.JobRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Capa de Aplicación: lógica de análisis de sentimiento y extracción de keywords.
 * Orquesta lectura/escritura en BD sin guardar estado en memoria (stateless).
 */
@Service
public class AnalysisService {

    /** Pausa entre pasos del panel / (solo demo visual, en ms) */
    private static final long DEMO_STEP_DELAY_MS = 1200;

    private final JobRepository jobRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Listas simples para análisis de sentimiento en español
    private static final Set<String> POSITIVE_WORDS = Set.of(
            "bueno", "buena", "excelente", "genial", "fantastico", "maravilloso",
            "feliz", "alegre", "amor", "positivo", "increible", "perfecto", "gracias"
    );
    private static final Set<String> NEGATIVE_WORDS = Set.of(
            "malo", "mala", "terrible", "horrible", "triste", "odio", "negativo",
            "pesimo", "decepcion", "enojo", "molesto", "fatal", "problema"
    );

    // Stopwords en español para extracción de keywords
    private static final Set<String> STOPWORDS = Set.of(
            "el", "la", "los", "las", "un", "una", "de", "del", "en", "y", "o",
            "a", "que", "es", "son", "con", "por", "para", "se", "su", "sus",
            "me", "te", "lo", "le", "al", "como", "muy", "mas", "pero", "si", "no"
    );

    public AnalysisService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * Procesa un job: PROCESANDO → análisis → COMPLETADO.
     * Llamado por el Controller cuando Python notifica vía REST.
     */
    @Transactional
    public Job analyzeJob(UUID jobId) {
        ActivityTracker.onReceived(jobId.toString());
        pause(DEMO_STEP_DELAY_MS);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job no encontrado: " + jobId));

        String preview = job.getText().length() > 60
                ? job.getText().substring(0, 60) + "..." : job.getText();
        ActivityTracker.onReadingDb(preview);
        pause(DEMO_STEP_DELAY_MS);

        ActivityTracker.onProcessing();
        job.setStatus(JobStatus.PROCESANDO);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
        pause(DEMO_STEP_DELAY_MS);

        ActivityTracker.onAnalyzingSentiment();
        SentimentResult sentiment = analyzeSentiment(job.getText());
        pause(DEMO_STEP_DELAY_MS);

        ActivityTracker.onExtractingKeywords();
        List<String> keywords = extractKeywords(job.getText());
        pause(DEMO_STEP_DELAY_MS);

        ActivityTracker.onSaving(sentiment.label(), keywords.toString());
        job.setSentiment(sentiment.label());
        job.setScore(sentiment.score());
        job.setKeywords(toJson(keywords));
        job.setStatus(JobStatus.COMPLETADO);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
        pause(DEMO_STEP_DELAY_MS);

        ActivityTracker.onFinished(sentiment.label(), sentiment.score());
        return job;
    }

    /** Pausa breve para que el panel / muestre cada paso interno (solo demo). */
    private void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private SentimentResult analyzeSentiment(String text) {
        String normalized = text.toLowerCase()
                .replaceAll("[^a-záéíóúüñ\\s]", " ");
        String[] words = normalized.split("\\s+");

        int positive = 0;
        int negative = 0;
        for (String word : words) {
            if (POSITIVE_WORDS.contains(word)) positive++;
            if (NEGATIVE_WORDS.contains(word)) negative++;
        }

        String label;
        double score;
        if (positive > negative) {
            label = "POSITIVO";
            score = Math.min(1.0, 0.5 + (positive - negative) * 0.1);
        } else if (negative > positive) {
            label = "NEGATIVO";
            score = Math.max(-1.0, -0.5 - (negative - positive) * 0.1);
        } else {
            label = "NEUTRAL";
            score = 0.0;
        }
        return new SentimentResult(label, score);
    }

    private List<String> extractKeywords(String text) {
        String normalized = text.toLowerCase()
                .replaceAll("[^a-záéíóúüñ\\s]", " ");
        Map<String, Long> frequency = Arrays.stream(normalized.split("\\s+"))
                .filter(w -> w.length() > 2)
                .filter(w -> !STOPWORDS.contains(w))
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        return frequency.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private String toJson(List<String> keywords) {
        try {
            return objectMapper.writeValueAsString(keywords);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private record SentimentResult(String label, double score) {}
}
