package com.analyticore.analyseservice.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada Spring Boot (capa Infraestructura).
 */
@SpringBootApplication(scanBasePackages = "com.analyticore.analyseservice")
public class AnalysisServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalysisServiceApplication.class, args);
    }
}
