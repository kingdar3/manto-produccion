# Explicacion del flujo y base de datos

Este documento explica como la app movil y el backend usan la base de datos durante los flujos principales. Cada seccion describe el proceso, los endpoints implicados, las tablas que se modifican o consultan y ejemplos de datos que verias en la base de datos.

## 1. Registro de usuario

En la app, el usuario completa el formulario de registro. El backend valida los datos y crea un registro base en la tabla `users`. Este registro es la identidad interna del sistema y se usara luego para vinculos, grupos familiares, emergencias y notificaciones.

**App**: pantalla de registro
**Backend**: `POST /api/v1/users`
**Tabla**: `users`

Campos relevantes y ejemplo:

- `id`: UUID interno del usuario. Ejemplo: `b3f7b4c0-6b4d-4c2f-9b76-2fb2a1f1e111`
- `name`: nombre mostrado. Ejemplo: `Juan Perez`
- `email`: usado para buscar el usuario al iniciar sesion. Ejemplo: `juan@gmail.com`
- `phone`: contacto. Ejemplo: `+51 987654321`
- `active`: estado. Ejemplo: `true`
- `created_at`: fecha de registro. Ejemplo: `2026-05-07 17:00`

## 2. Inicio de sesion

La autenticacion principal se realiza con Firebase. La app recibe el token de Firebase y luego consulta al backend para obtener el `id` interno del usuario. En esta etapa no se crea un nuevo usuario si ya existe; se consulta la tabla `users` y se carga el perfil correspondiente.

**App**: pantalla de login
**Backend**: consulta por email
**Tabla**: `users`

Campos consultados:

- `email`: clave para ubicar al usuario.
- `id`: se usa en todo el resto del flujo.

## 3. Generar codigo de invitacion (8 caracteres)

En modo host, el usuario genera un codigo corto para invitar a un protegido. La app muestra un token alfanumerico de 8 caracteres. En la base de datos se registra una fila en `invitations`. Esta fila actua como puerta de entrada a la vinculacion.

**App**: boton "Generar Codigo"
**Backend**: `POST /api/v1/invitations`
**Tabla**: `invitations`

Campos y ejemplo:

- `id`: `c1a2d3e4-1111-2222-3333-444455556666`
- `token`: codigo mostrado al host. Ejemplo: `A4B92XK7`
- `host_id`: `uuid_host`
- `host_name`: `Juan Perez`
- `status`: `PENDING`
- `expires_at`: `2026-05-07 18:00`
- `created_at`: `2026-05-07 17:10`
- `accepted_at`: se completa cuando el protegido acepta. Ejemplo: `2026-05-07 17:20`
- `accepted_by_user_id`: `uuid_protected`

## 4. Ingreso del codigo de 8 caracteres

El protegido escribe el codigo de 8 caracteres en la app. El backend valida que el token exista, no haya expirado y este en estado `PENDING`. En ese momento se marca la invitacion como aceptada y se inicia el proceso de vinculacion.

**App**: pantalla de ingreso de codigo
**Backend**: `POST /api/v1/invitations/{token}/accept`
**Tabla**: `invitations`

Cambios tipicos:

- `status`: pasa de `PENDING` a `ACCEPTED`
- `accepted_at`: se llena con la fecha actual
- `accepted_by_user_id`: se llena con el usuario protegido

## 5. Confirmacion con PIN de 6 digitos

Despues de aceptar la invitacion, el sistema genera un PIN de 6 digitos. Este PIN vive en la tabla `links` y sirve para confirmar el vinculo entre host y protegido. El protegido lo ve en la app y el host lo confirma. Una vez confirmado, el vinculo queda activo.

**App**: pantalla del PIN
**Backend**: `POST /api/v1/links/{linkId}/confirm`
**Tabla**: `links`

Campos y ejemplo:

- `id`: `link_uuid`
- `host_id`: `uuid_host`
- `protected_id`: `uuid_protected`
- `connection_code`: PIN de 6 digitos. Ejemplo: `834920`
- `code_created_at`: cuando se genera el PIN. Ejemplo: `2026-05-07 17:11`
- `code_expires_at`: cuando expira el PIN. Ejemplo: `2026-05-07 17:21`
- `confirmed_at`: cuando se valida el PIN. Ejemplo: `2026-05-07 17:12`
- `status`: `PENDING` -> `ACTIVE`
- `created_at`: `2026-05-07 17:11`
- `updated_at`: ultimo cambio, por ejemplo al confirmar

## 6. Gestion de familias

En el modo host, la pantalla "Gestionar familias" permite crear un grupo familiar y generar invitaciones para hosts secundarios o protegidos adicionales. En la base de datos, se crean registros en `family_groups` y se agregan miembros en `family_group_members`. Las invitaciones de familia se guardan en `family_invitations` y funcionan de forma similar a las invitaciones normales, pero con un rol destino.

**App**: Gestionar circulo familiar
**Backend**: `POST /api/v1/families`, `POST /api/v1/family-invitations`
**Tablas**: `family_groups`, `family_group_members`, `family_invitations`

`family_groups`:

- `id`: `fg_uuid`
- `name`: `Familia Perez`
- `primary_host_user_id`: `uuid_host`
- `created_at`: `2026-05-07 17:30`

`family_group_members`:

- `id`: `fgm_uuid`
- `family_group_id`: `fg_uuid`
- `user_id`: `uuid_user`
- `role`: `PRIMARY_HOST` / `SECONDARY_HOST` / `PROTECTED`
- `joined_at`: `2026-05-07 17:35`

`family_invitations`:

- `id`: `fi_uuid`
- `family_group_id`: `fg_uuid`
- `invited_by_user_id`: `uuid_host`
- `token`: `HJ7KLM2R`
- `target_role`: `SECONDARY_HOST`
- `status`: `PENDING` / `ACCEPTED`
- `created_at`: `2026-05-07 17:36`
- `expires_at`: `2026-05-07 18:36`
- `accepted_at`: `2026-05-07 17:50`
- `accepted_by_user_id`: `uuid_user`

## 7. Alertas de navegacion sospechosa

Cuando el protegido navega a una URL detectada como sospechosa, la app envia una alerta al backend. Esta alerta se guarda en `alerts` y queda visible para el host, quien puede resolverla y dejar una nota.

**App**: navegador seguro
**Backend**: `POST /api/v1/alerts`
**Tabla**: `alerts`

Campos y ejemplo:

- `id`: `alert_uuid`
- `link_id`: `link_uuid`
- `protected_user_id`: `uuid_protected`
- `suspicious_url`: `http://fake-bank.com`
- `reason`: `URL sospechosa`
- `status`: `PENDING` / `RESOLVED`
- `created_at`: `2026-05-07 18:10`
- `resolved_at`: `2026-05-07 18:12`
- `resolved_by_user_id`: `uuid_host`
- `resolution_note`: `Bloqueado`

## 8. Emergencia SOS

Cuando el protegido presiona el boton SOS, el backend crea una emergencia y guarda la ubicacion. Los hosts reciben notificacion y pueden resolver la emergencia desde la app. El estado y la resolucion se registran en `emergency_alerts`.

**App**: boton SOS
**Backend**: `POST /api/v1/emergencies`
**Tabla**: `emergency_alerts`

Campos y ejemplo:

- `id`: `em_uuid`
- `link_id`: `link_uuid`
- `protected_user_id`: `uuid_protected`
- `primary_host_user_id`: `uuid_host`
- `latitude`: `-12.04`
- `longitude`: `-77.03`
- `status`: `ACTIVE` / `RESOLVED`
- `created_at`: `2026-05-07 18:20`
- `resolved_at`: `2026-05-07 18:25`
- `resolved_by_user_id`: `uuid_host`
- `resolution_type`: `ALL_SAFE` / `FALSE_ALARM` / `POLICE_SENT`
- `resolution_note`: `Todo bien`

## 9. Audio de emergencia

Durante una emergencia, la app protegida puede transmitir o subir audio. El backend guarda un registro por cada subida y genera una URL de reproduccion. El host escucha el audio desde la app y el registro queda almacenado en `emergency_audio_recordings`.

**App**: grabacion y subida de audio
**Backend**: `POST /api/v1/emergencies/{id}/audio`
**Tabla**: `emergency_audio_recordings`

Campos y ejemplo:

- `id`: `audio_uuid`
- `emergency_alert_id`: `em_uuid`
- `created_at`: `2026-05-07 18:20`
- `uploaded_at`: `2026-05-07 18:25`
- `duration_seconds`: `240`
- `file_size_bytes`: `7000000`
- `storage_provider`: `LOCAL` / `GOOGLE_DRIVE`
- `storage_file_id`: `emergency-xxx.wav`
- `playback_url`: `/uploads/emergency-audio/...wav`
- `status`: `RECORDING` / `UPLOADED` / `FAILED`

## 10. device_tokens (notificaciones push)

Cada vez que un usuario inicia sesion, la app registra el token de Firebase Cloud Messaging. El backend guarda este token para poder enviar notificaciones cuando ocurre una alerta, emergencia o verificacion. Si el usuario reinstala la app o el token cambia, se actualiza el registro.

**App**: registro de dispositivo
**Backend**: `POST /api/v1/notifications/token`
**Tabla**: `device_tokens`

Campos y ejemplo:

- `id`: `dt_uuid`
- `user_id`: `uuid_user`
- `token`: `eYz...`
- `platform`: `ANDROID` / `IOS`
- `created_at`: `2026-05-07 17:01`
- `updated_at`: `2026-05-07 17:40`

Cuando el backend necesita notificar, busca los tokens asociados a los usuarios destino y llama a FCM con esos tokens.
