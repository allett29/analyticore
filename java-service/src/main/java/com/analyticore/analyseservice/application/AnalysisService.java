package com.analyticore.analyseservice.application;

import com.analyticore.analyseservice.domain.Job;
import com.analyticore.analyseservice.domain.JobStatus;
import com.analyticore.analyseservice.domain.port.JobRepositoryPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Capa de Aplicación — Inicia el análisis (síncrono) y lo completa (asíncrono).
 *
 * Flujo:
 *   startAnalysis()  → REST síncrono desde Python: marca PROCESANDO y responde
 *   completeAnalysis() → worker asíncrono: analiza y marca COMPLETADO en PostgreSQL
 */
@Service
public class AnalysisService {

    private final JobRepositoryPort jobRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Set<String> POSITIVE_WORDS = Set.of(
            "bueno", "buena", "excelente", "genial", "fantastico", "maravilloso",
            "feliz", "alegre", "amor", "positivo", "increible", "perfecto", "gracias"
    );
    private static final Set<String> NEGATIVE_WORDS = Set.of(
            "malo", "mala", "terrible", "horrible", "triste", "odio", "negativo",
            "pesimo", "decepcion", "enojo", "molesto", "fatal", "problema"
    );
    private static final Set<String> STOPWORDS = Set.of(
            "el", "la", "los", "las", "un", "una", "de", "del", "en", "y", "o",
            "a", "que", "es", "son", "con", "por", "para", "se", "su", "sus",
            "me", "te", "lo", "le", "al", "como", "muy", "mas", "pero", "si", "no"
    );

    public AnalysisService(JobRepositoryPort jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public Job startAnalysis(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job no encontrado: " + jobId));

        if (job.getStatus() != JobStatus.PENDIENTE) {
            throw new IllegalStateException("El job no está en estado PENDIENTE: " + job.getStatus());
        }

        job.setStatus(JobStatus.PROCESANDO);
        job.setUpdatedAt(LocalDateTime.now());
        return jobRepository.save(job);
    }

    @Transactional
    public void completeAnalysis(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job no encontrado: " + jobId));

        SentimentResult sentiment = analyzeSentiment(job.getText());
        List<String> keywords = extractKeywords(job.getText());

        job.setSentiment(sentiment.label());
        job.setScore(sentiment.score());
        job.setKeywords(toJson(keywords));
        job.setStatus(JobStatus.COMPLETADO);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
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
