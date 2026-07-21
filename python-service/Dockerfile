# Imagen de producción para el Servicio de Submisión (Python/FastAPI)
FROM python:3.12-slim

WORKDIR /app

# Dependencias del sistema para psycopg2
RUN apt-get update && apt-get install -y --no-install-recommends \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# Puerto expuesto en Render y docker-compose
EXPOSE 8000

# Servidor ASGI de producción
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
