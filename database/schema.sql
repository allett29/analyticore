-- Esquema compartido de PostgreSQL para AnalytiCore
-- Usado por python-service (creación) y java-service (lectura/actualización)

CREATE TABLE IF NOT EXISTS jobs (
    id          UUID PRIMARY KEY,
    text        TEXT NOT NULL,
    status      VARCHAR(20) NOT NULL,
    sentiment   VARCHAR(20),
    score       DOUBLE PRECISION,
    keywords    TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_jobs_status ON jobs(status);
