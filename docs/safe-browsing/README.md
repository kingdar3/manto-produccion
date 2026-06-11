# Google Safe Browsing - Contrato de Negocio (v1)

## 1. Objetivo
Definir el contrato funcional para analizar enlaces recibidos desde Android y clasificar riesgo de phishing/fraude usando Google Safe Browsing.

## 2. Contexto de entrada
El cliente Android enviará un payload con:
- `message`: texto del SMS/notificación recibida.
- `urls`: lista de URLs extraídas.
- `sender`: emisor del mensaje.

Ejemplo de entrada (referencial):
- `message`: "BBVA: su cuenta fue bloqueada..."
- `urls`: ["http://bbva-seguridad.xyz"]
- `sender`: "+51999999999"

## 3. Alcance del Release 1 (MVP)
- Se analiza únicamente `urls[]` contra Google Safe Browsing.
- `message` y `sender` se reciben pero no participan en el scoring en esta versión.
- Se aplicará un límite máximo de URLs por request (sugerido: 20).
- Se eliminarán URLs duplicadas antes de consultar a Google.

## 4. Contrato de salida (negocio)
La API de backend debe devolver:
- Resultado por URL.
- Resultado global del request.
- Metadatos de auditoría básicos (`source`, `detectedAt`).

Estados internos por URL:
- `SAFE`: sin amenazas detectadas.
- `PHISHING`: detectado como ingeniería social/fraude.
- `MALWARE`: detectado como malware.
- `UNWANTED`: software no deseado/potencialmente dañino.
- `SUSPICIOUS`: señal de riesgo no concluyente.
- `ERROR`: no fue posible analizar técnicamente.

Resultado global del request:
- `FRAUD_RISK` si al menos una URL está en `PHISHING`.
- `MALWARE_RISK` si no hay `PHISHING`, pero sí `MALWARE`.
- `UNWANTED_RISK` si no hay anteriores, pero sí `UNWANTED`.
- `SUSPICIOUS_RISK` si no hay anteriores, pero sí `SUSPICIOUS`.
- `NO_RISK_DETECTED` si todas son `SAFE`.
- `PARTIAL_ANALYSIS` si hubo mezcla de `SAFE`/riesgo con una o más `ERROR`.
- `ANALYSIS_ERROR` si no pudo analizarse ninguna URL.

## 5. Reglas de decisión
- Mapeo crítico:
  - Si Google reporta `SOCIAL_ENGINEERING` => `PHISHING`.
  - Si Google reporta `MALWARE` => `MALWARE`.
  - Si Google reporta `UNWANTED_SOFTWARE` => `UNWANTED`.
  - Tipos no mapeados explícitamente => `SUSPICIOUS`.
- Priorización de severidad para resultado global:
  - `PHISHING` > `MALWARE` > `UNWANTED` > `SUSPICIOUS` > `SAFE`.
- Si la misma URL retorna múltiples amenazas, gana la de mayor severidad.

## 6. Reglas de validación de entrada
- `urls` es obligatorio y debe tener al menos 1 elemento.
- Cada URL debe tener formato válido HTTP/HTTPS.
- Se hace `trim` de espacios.
- URLs inválidas no se envían a Google y se marcan como `ERROR` con motivo de validación.
- Si todas son inválidas => `ANALYSIS_ERROR`.

## 7. Reglas operativas y resiliencia
- Tiempo de espera de integración a Google: corto (2-5 segundos).
- Si Google responde 429/5xx o timeout:
  - No romper el endpoint.
  - Responder estado funcional (`PARTIAL_ANALYSIS` o `ANALYSIS_ERROR` según corresponda).
  - Registrar evento técnico para observabilidad.
- Nunca exponer la API key en logs o respuestas.

## 8. Trazabilidad y auditoría mínima
Cada análisis debe registrar internamente:
- `requestId`.
- Fecha/hora de análisis (`detectedAt`).
- Cantidad de URLs recibidas, válidas, inválidas y analizadas.
- Resultado global.

## 9. Exclusiones de esta versión
Fuera de alcance en v1:
- Clasificación semántica del texto `message` (NLP/LLM).
- Reputación del `sender`.
- Enriquecimiento con listas negras propias.
- Persistencia histórica avanzada y paneles de riesgo.

## 10. Criterios de aceptación de negocio
- Dado un request con una URL phishing conocida, el resultado global debe ser `FRAUD_RISK`.
- Dado un request con URLs limpias, el resultado global debe ser `NO_RISK_DETECTED`.
- Dado un fallo de Google con al menos una URL válida, el endpoint debe responder sin 500 y marcar análisis parcial o error controlado.
- Dado un request con URLs inválidas, deben reportarse explícitamente como error de validación por URL.

## 11. Fuente de verdad
Este documento es la referencia funcional para:
- Definir puertos de dominio (`in`/`out`).
- Diseñar DTOs de request/response.
- Implementar reglas de aplicación.
- Construir pruebas funcionales y de integración.
