# Catalogo de Controles de Seguridad

## Categoria: Autenticacion y Autorizacion
- Control: Autenticacion de usuarios con Firebase Auth.
- Estado: Implementado en app.
- Mejora: Validacion de token en backend.

## Categoria: Integridad de Datos
- Control: Validaciones de entrada en DTOs.
- Estado: Implementado.
- Mejora: Sanitizacion adicional para strings extensos.

## Categoria: Disponibilidad
- Control: Backups definidos (pg_dump).
- Estado: Documentado.
- Mejora: Automatizacion de respaldos.

## Categoria: Confidencialidad
- Control: TLS/HTTPS para trafico.
- Estado: Recomendado (ngrok).
- Mejora: Certificado en entorno productivo.

## Categoria: Auditoria
- Control: Timestamps en entidades.
- Estado: Implementado.
- Mejora: Registro de eventos criticos.
