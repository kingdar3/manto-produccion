# Informe Tecnico de Seguridad y Cifrado

## Objetivo
Documentar medidas de seguridad y cifrado aplicadas, incluyendo controles propuestos y practicas recomendadas para confidencialidad, integridad y disponibilidad (CIA).

## Autenticacion y autorizacion
- Autenticacion en app movil via Firebase Auth.
- Tokens de dispositivo (FCM) asociados a usuarios.
- Recomendacion: validar tokens en backend (middleware) para endpoints sensibles.

## Cifrado y proteccion de datos
- Transito: HTTPS recomendado (ngrok/HTTPS en pruebas).
- Almacenamiento: contraseñas no se almacenan en BD (Firebase gestiona credenciales).
- Campos sensibles: se recomienda cifrado en repositorio si se almacenan datos adicionales.

## Controles de seguridad aplicados
- Validaciones de entrada en DTOs (Bean Validation).
- Control de roles para resolver emergencias (solo host principal, o policy definida).
- Auditoria basica con timestamps en entidades.

## Riesgos y mitigaciones
- Exposicion de endpoints: aplicar autenticacion backend.
- Tamaño de uploads: limites configurados.
- Datos personales: minimizar exposicion y uso de HTTPS.

## Recomendaciones adicionales
- Spring Security con JWT o validacion de Firebase tokens.
- Rotacion de credenciales y secretos fuera del repo.
- Logging con mascarado de datos sensibles.
