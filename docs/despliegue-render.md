# Guía de despliegue en Render

Sigue estos pasos en orden para desplegar AnalytiCore en la nube.

## Paso 1: Subir el código a GitHub

1. Crea un repositorio en GitHub (ej: `analyticore`).
2. En la carpeta del proyecto, ejecuta:

```bash
git init
git add .
git commit -m "Prototipo AnalytiCore: arquitectura SOA políglota"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/analyticore.git
git push -u origin main
```

## Paso 2: Crear PostgreSQL en Render

1. Entra a [render.com](https://render.com) → **New** → **PostgreSQL**.
2. Nombre: `analyticore-db`
3. Plan: Free
4. Crea la base de datos.
5. Copia la **Internal Database URL** (la usarán los servicios).

## Paso 3: Desplegar Java Service

1. **New** → **Web Service** → conecta tu repo de GitHub.
2. Configuración:
   - **Name**: `analyticore-java`
   - **Root Directory**: `java-service`
   - **Runtime**: Docker
   - **Instance Type**: Free
3. Variables de entorno:
   - `DATABASE_URL` = Internal Database URL de PostgreSQL
4. Crea el servicio y espera el deploy.
5. Copia la URL pública (ej: `https://analyticore-java.onrender.com`).

## Paso 4: Desplegar Python Service

1. **New** → **Web Service** → mismo repo.
2. Configuración:
   - **Name**: `analyticore-python`
   - **Root Directory**: `python-service`
   - **Runtime**: Docker
   - **Instance Type**: Free
3. Variables de entorno:
   - `DATABASE_URL` = Internal Database URL de PostgreSQL
   - `JAVA_SERVICE_URL` = URL del java-service (paso 3)
4. Crea el servicio y copia su URL pública.

## Paso 5: Desplegar Frontend

1. **New** → **Web Service** → mismo repo.
2. Configuración:
   - **Name**: `analyticore-frontend`
   - **Root Directory**: `frontend`
   - **Runtime**: Docker
   - **Instance Type**: Free
3. Variables de entorno (Docker build arg):
   - `VITE_API_URL` = URL del python-service (paso 4)
4. En **Advanced** → agregar build arg si Render lo pide:
   - `VITE_API_URL` = `https://analyticore-python.onrender.com`
5. Crea el servicio.

## Paso 6: Verificar

1. Abre la URL del frontend.
2. Escribe un texto (ej: "Este producto es excelente y me encanta").
3. Verifica que aparecen sentimiento POSITIVO y palabras clave.

## Notas

- El plan Free de Render "duerme" los servicios tras inactividad. La primera petición puede tardar ~30s.
- No se requiere Docker Hub: Render construye las imágenes directamente desde el repo.
- El esquema de BD se crea automáticamente al arrancar el python-service.
