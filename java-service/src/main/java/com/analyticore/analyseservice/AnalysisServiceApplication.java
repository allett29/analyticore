package com.analyticore.analyseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del Servicio de Análisis (Java/Spring Boot).
 *
 * BUS DE COMUNICACIÓN: REST/HTTP
 *
 * Conexiones de este servicio:
 *   ENTRADA  ← Python (java_client.py línea 27) : POST /api/analyze/{jobId}
 *   SALIDA   → PostgreSQL (Render)               : JDBC/JPA vía repository/JobRepository.java
 *   (No comunica directamente con el Frontend)
 */
@SpringBootApplication
public class AnalysisServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalysisServiceApplication.class, args);
    }
}
