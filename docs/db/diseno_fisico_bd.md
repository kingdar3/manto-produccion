# Diseño fisico de base de datos

## Artefactos
- Script SQL (DDL): `src/main/resources/db/schema.sql`
- Diagrama ER: `docs/erd/guardian_db_erd.png`

## Motor y configuracion
- Motor: PostgreSQL 16 (contenedor Docker)
- Charset: UTF-8
- Estrategia de generacion: entidades JPA + export DDL desde Postgres
- DDL versionado para asegurar consistencia y trazabilidad

## Modelo fisico (tablas)
### Seguridad y usuarios
- `users`: usuarios del sistema (host, protegido, secundario)
- `device_tokens`: tokens FCM por usuario

### Vinculos y familias
- `links`: vinculos host-protegido (codigo, estado)
- `invitations`: invitaciones de vinculo
- `family_groups`: grupo familiar
- `family_group_members`: miembros y roles
- `family_invitations`: invitaciones de familia y rol

### Operaciones y alertas
- `alerts`: alertas de navegacion sospechosa
- `emergency_alerts`: emergencias (ubicacion, resolucion)
- `emergency_audio_recordings`: audios asociados

## Tipos de datos
- `uuid`: claves primarias y foraneas
- `timestamp(6)`: fechas de creacion/actualizacion
- `varchar`: estados, tokens, descripciones
- `bool`: flags (ej. `users.active`)

## Relaciones clave
- `alerts.link_id` -> `links.id`
- `links.host_id`, `links.protected_id` -> `users.id`
- `emergency_alerts.link_id` -> `links.id`
- `emergency_alerts.primary_host_user_id`, `emergency_alerts.protected_user_id` -> `users.id`
- `emergency_audio_recordings.emergency_alert_id` -> `emergency_alerts.id`
- `family_group_members.family_group_id` -> `family_groups.id`
- `family_group_members.user_id` -> `users.id`

## Indices
Se incluyen indices para busquedas por usuario, estado y emergencia:
- `idx_emergency_host_status`, `idx_emergency_protected`
- `idx_alert_status`, `idx_alert_link_id`
- `idx_family_member_group`, `idx_family_member_user`
- `idx_device_token_user`

## Reglas y constraints
- Checks de estados (ej. `links.status`, `alerts.status`, `emergency_alerts.status`)
- Unicos por token e email
- Integridad referencial con foreign keys

## Consistencia modelo vs SQL
El modelo JPA (entidades) y el SQL exportado coinciden con:
- nombres de tablas y columnas
- tipos principales (uuid, timestamp, varchar, bool)
- constraints y checks de enums

Este archivo es el respaldo oficial del diseño fisico y se usa como evidencia.
