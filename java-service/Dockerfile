# Imagen de producción para el Servicio de Análisis (Java/Spring Boot)
# Etapa 1: compilar el JAR con Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: ejecutar el JAR en runtime mínimo
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/analysis-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
