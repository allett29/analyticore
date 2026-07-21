# Informe Ejecutivo — AnalytiCore

## Problema de negocio

AnalytiCore es una startup de análisis de datos que necesita ofrecer a sus clientes un servicio en línea para analizar textos de forma automática. El reto consiste en procesar grandes volúmenes de solicitudes, extraer información útil (sentimiento y palabras clave) y entregar resultados de manera confiable, sin depender de una única tecnología ni de infraestructura local.

## Solución propuesta y su valor

Se diseñó una plataforma cloud compuesta por tres microservicios independientes: un frontend web (React), un servicio de submisión (Python) y un servicio de análisis (Java), todos conectados a una base de datos PostgreSQL centralizada. El usuario envía un texto, el sistema lo procesa automáticamente y devuelve el sentimiento detectado junto con las palabras clave más relevantes. Esta solución demuestra la viabilidad técnica de un producto real con arquitectura políglota, lista para escalar según la demanda.

## Beneficios de la arquitectura elegida

- **Escalabilidad**: cada servicio puede crecer de forma independiente. Si aumenta la carga de análisis, solo se escala el servicio Java sin afectar al frontend ni al servicio de submisión.
- **Mantenibilidad**: los equipos pueden modificar o actualizar un componente sin romper los demás, gracias a la comunicación exclusiva por APIs REST y al estado centralizado en la base de datos.
- **Flexibilidad del equipo**: al usar React, Python y Java, diferentes especialistas pueden trabajar en paralelo en su área de expertise, acelerando el desarrollo y facilitando futuras expansiones del producto.
